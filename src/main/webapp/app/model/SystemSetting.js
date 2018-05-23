Ext.define('BSP.model.SystemSetting', {
    extend: 'Ext.data.Model',

    idProperty: 'name',
    fields: [
        {name: "name"},
        {name: "value"}
    ],

    proxy: {
        type: 'ajax',
        api: {
            read: 'mvc/system/setting',
            update: 'mvc/system/setting/update'
        },
        reader: {
            type: 'json',
            rootProperty: 'children'
        }
    }
});
