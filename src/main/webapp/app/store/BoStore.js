Ext.define('BSP.store.BoStore',{
    extend: 'Ext.data.Store',
    model: 'BSP.model.Bo',
    autoLoad: true,
    storeId: 'BoStore',
    sorters: [{
        property: 'createdDate',
        direction: 'DESC'
    }]
});
