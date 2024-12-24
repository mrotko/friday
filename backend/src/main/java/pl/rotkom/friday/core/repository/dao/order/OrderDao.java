package pl.rotkom.friday.core.repository.dao.order;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import pl.rotkom.friday.core.repository.common.ExternalEntityDao;
import pl.rotkom.friday.core.repository.model.order.Order;

@Repository
class OrderDao extends ExternalEntityDao<Order> implements IOrderDao {

    public OrderDao(EntityManager entityManager) {
        super(Order.class, entityManager);
    }
}
