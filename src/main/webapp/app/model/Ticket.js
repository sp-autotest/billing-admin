Ext.define('BSP.model.Ticket', {
    extend: 'Ext.data.Model',

    requires: [
        'Ext.data.field.Date',
        'Ext.data.field.Integer',
        'Ext.data.proxy.Ajax'
    ],
    idProperty: 'id',
    fields: [
        {
            //type: 'int',
            name: 'id'
        },
        {
            name: 'documentNumber'
        },
        {
            name: 'transactionType'
        },
        {
            name: 'documentNumber'
        },
        {
            name: 'approvalCode'
        },
        {
            name: 'pan'
        },
        {
            name: 'refNum'
        },
        {
            name: 'amount'
        },
        {
            name: 'currency'
        },
        {
            name: 'documentNumber'
        },
        {
            type: 'date',
            name: 'createdAt'
        },
        {
            name: 'invoiceNumber'
        },
        {
            name: 'fileName'
        },
        {
            name: 'fileType'
        }

    ],
    proxy: {
        type: 'ajax',
        api: {
            read: 'mvc/ticket/read'
        }
    }
});

