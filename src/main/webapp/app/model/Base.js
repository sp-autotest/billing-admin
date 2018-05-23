/**
 * This class is the base class for all entities in the application.
 */
Ext.define('BSP.model.Base', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'int'
    }],

    schema: {
        namespace: 'BSP.model',
        proxy: {
            url: '{prefix}/{entityName:uncapitalize}',
            pageParam: '',
            startParam: '',
            limitParam: ''
        }
    }
});
