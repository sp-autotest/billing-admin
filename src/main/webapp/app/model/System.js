Ext.define('BSP.model.System', {
    extend: 'Ext.data.Model',

    idProperty: 'id',
    fields: [
        {name: "name"},
        {name: "value"}
    ],

    proxy: {
        type: 'ajax',
        api: {
            read: 'mvc/system/read'
        },
        reader: {
            type: 'json',
            rootProperty: 'children'
        }
    }
});
