Ext.define('BSP.store.TicketStore', {
    extend: 'Ext.data.TreeStore',
    model: 'BSP.model.Ticket',

    requires: [
        'BSP.model.Ticket'
    ],
    sorters: [{
        property: 'id',
        direction: 'DESC'
    }],
    storeId: 'TicketStore',
    autoLoad: false,
    root: {
        loaded: true //надо для того, чтобы срабатывал  autoLoad: false, иначе всё равно происходит запрос на чтение данных
    }
});
