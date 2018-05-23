Ext.define('BSP.model.BillingSystem', {
    extend: 'Ext.data.Model',

    requires: [
        'Ext.data.proxy.Rest'
    ],

    idProperty: 'id',
    fields: [
        {
            type: 'number',
            persist: false,
            name: 'id'
        },
        {
            type: 'string',
            name: 'name'
        },
        {
            type: 'string',
            name: 'carrierId'
        },
        {
            type: 'date',
            name: 'createdDate'
        },
        {
            type: 'string',
            name: 'host'
        },
        {
            type: 'string',
            name: 'port'
        },
        {
            type: 'string',
            name: 'path'
        }, {
            type: 'string',
            name: 'login'
        },
        {
            type: 'string',
            name: 'password'
        },
        {
            type: 'string',
            name: 'emailsCSV'
        },
        {
            type: 'string',
            name: 'maskRegexp'
        },
        {
            type: 'boolean',
            name: 'enabled'
        }
    ],

    proxy: {
        type: 'rest',
        url: 'mvc/billingSystem',
        reader: {
            type: 'json',
            rootProperty: 'data'
        }
    }
});
