Ext.define('BSP.model.User', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        {
            type: 'int',
            name: 'id'
        },
        {
            name: 'username'
        }
    ],

    proxy: {
        type: 'ajax',
        api: {
            read: 'mvc/user/read'
        },
        reader: {
            type: 'json',
            rootProperty: 'children'
        }
    }

}); 