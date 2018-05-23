Ext.define('BSP.model.Billing', {
    extend: 'Ext.data.Model',

    requires: [
        'Ext.data.field.Date',
        'Ext.data.field.Integer',
        'Ext.data.proxy.Ajax'
    ],
    idProperty: 'id',
    fields: [
        {
            type: 'int',
            name: 'id'
        },
        {
            name: 'name'
        },
        {
            name: 'fileType'
        },
        {
            type: 'date',
            format: 'Y/m/d H:i:s',
            name: 'businessDate'
        },
        {
            type: 'date',
            format: 'Y/m/d H:i:s',
            name: 'createdDate'
        },
        {
            name: 'format'
        }
    ],

    proxy: {
        type: 'ajax',
        api: {
            read: 'mvc/billing/read',
            create: 'mvc/billing/create',
            update: 'mvc/billing/update',
            destroy: 'mvc/billing/delete'
        }
    }
});
