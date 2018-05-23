Ext.define('BSP.store.BillingStore', {
    extend: 'Ext.data.TreeStore',
    model: 'BSP.model.Billing',

    requires: [
        'BSP.model.Billing'
    ],
    sorters: [{
        property: 'id',
        direction: 'DESC'
    }],
    storeId: 'BillingStore',
    autoLoad: true

    //nodeParam: 'id'
});
