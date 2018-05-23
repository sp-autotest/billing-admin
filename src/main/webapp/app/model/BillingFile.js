Ext.define('BSP.model.BillingFile',{
    extend: 'BSP.model.Base',

    fields: [
        'billingFieName',
        'fileType',
        {name:'businessDate',type: 'date'},
        {name:'uploadDate', type: 'date'},
        {name: 'format'}
    ],

    proxy: {
        type: 'ajax',
        api: {
            create : 'createBillingFile',
            read : 'readBillingFiles'
        }
    }

});
