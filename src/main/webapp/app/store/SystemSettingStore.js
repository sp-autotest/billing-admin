Ext.define('BSP.store.SystemSettingStore', {
    extend: 'Ext.data.Store',
    model: 'BSP.model.SystemSetting',
    storeId: 'SystemSettingStore',
    sorters: [{
        property: 'name',
        direction: 'ASC'
    }]
});

