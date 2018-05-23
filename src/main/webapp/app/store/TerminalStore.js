Ext.define('BSP.store.TerminalStore',{
    extend: 'Ext.data.Store',
    model: 'BSP.model.Terminal',
    autoLoad: true,
    storeId: 'TerminalStore',
    sorters: [{
        property: 'name',
        direction: 'DESC'
    }]
});
