package pl.rotkom.friday.core.repository.service.order;

import org.springframework.stereotype.Service;

import pl.rotkom.friday.core.repository.common.ExternalEntityService;
import pl.rotkom.friday.core.repository.dao.order.IOrderDao;
import pl.rotkom.friday.core.repository.model.order.Order;

@Service
class OrderService extends ExternalEntityService<Order> implements IOrderService {

    private final IOrderDao dao;

    public OrderService(IOrderDao dao) {
        super(dao);
        this.dao = dao;
    }
}
