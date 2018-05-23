Ext.define('BSP.model.Bo', {
    extend: 'Ext.data.Model',

    idProperty: 'fileName',
    fields: [
        {
            type: 'string',
            name: 'fileName'
        },
        {
            type: 'date',
            name: 'createdDate'
        },
        {
            type: 'int',
            name: 'size'
        }
    ],

    proxy: {
        type: 'ajax',
        api: {
            read: 'mvc/bo/read'
        },
        reader: {
            type: 'json',
            rootProperty: 'children'
        }
    }
});
