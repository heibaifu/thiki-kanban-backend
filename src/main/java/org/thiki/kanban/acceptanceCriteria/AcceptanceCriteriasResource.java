package org.thiki.kanban.acceptanceCriteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.thiki.kanban.card.CardsController;
import org.thiki.kanban.foundation.common.RestResource;
import org.thiki.kanban.foundation.hateoas.TLink;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by xubt on 10/17/16.
 */
@Service
public class AcceptanceCriteriasResource extends RestResource {
    public static Logger logger = LoggerFactory.getLogger(AcceptanceCriteriasResource.class);

    @Resource
    private TLink tlink;
    @Resource
    private AcceptanceCriteriaResource acceptanceCriteriaResourceService;

    @Cacheable(value = "acceptanceCriteria", key = "'acceptanceCriterias'+#boardId+#stageId+#cardId+#userName")
    public Object toResource(List<AcceptanceCriteria> acceptanceCriterias, String boardId, String stageId, String cardId, String userName) throws Exception {
        logger.info("build acceptanceCriterias resource.acceptanceCriterias:{},boardId:{},stageId:{},cardId:{},userName:{}", acceptanceCriterias, boardId, stageId, cardId, userName);
        AcceptanceCriteriasResource acceptanceCriteriasResource = new AcceptanceCriteriasResource();
        List<Object> acceptanceCriteriaResources = new ArrayList<>();
        for (AcceptanceCriteria acceptanceCriteria : acceptanceCriterias) {
            Object acceptanceCriteriaResource = acceptanceCriteriaResourceService.toResource(acceptanceCriteria, boardId, stageId, cardId, userName);
            acceptanceCriteriaResources.add(acceptanceCriteriaResource);
        }

        acceptanceCriteriasResource.buildDataObject("acceptanceCriterias", acceptanceCriteriaResources);
        Link selfLink = linkTo(methodOn(AcceptanceCriteriaController.class).loadAcceptanceCriteriasByCardId(boardId, stageId, cardId, userName)).withSelfRel();
        acceptanceCriteriasResource.add(tlink.from(selfLink).build(userName));

        Link movementLink = linkTo(methodOn(AcceptanceCriteriaController.class).resortAcceptCriterias(null, boardId, stageId, cardId, userName)).withRel("movement");
        acceptanceCriteriasResource.add(tlink.from(movementLink).build(userName));

        Link cardLink = linkTo(methodOn(CardsController.class).findById(boardId, stageId, cardId, userName)).withRel("card");
        acceptanceCriteriasResource.add(tlink.from(cardLink).build(userName));
        logger.info("acceptanceCriterias resource build completed.boardId:{},stageId:{},cardId:{},userName:{}", boardId, stageId, cardId, userName);
        return acceptanceCriteriasResource.getResource();
    }
}
