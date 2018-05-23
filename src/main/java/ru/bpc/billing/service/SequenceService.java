package ru.bpc.billing.service;

import org.hibernate.dialect.Dialect;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * User: Krainov
 * Date: 11.04.2016
 * Time: 15:27
 */
@Service
public class SequenceService {

    private final static String UTRNNO_SEQ_NAME = "bsp_fe_utrnno_seq";

    @Resource
    private Dialect currentDialect;
    @PersistenceContext
    private EntityManager entityManager;

    public long nextForUtrnno() {
        String sql = currentDialect.getSequenceNextValString(UTRNNO_SEQ_NAME);
        return ((Number)entityManager.createNativeQuery(sql).getSingleResult()).longValue();
    }
}
