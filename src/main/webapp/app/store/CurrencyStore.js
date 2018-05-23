Ext.define('BSP.store.CurrencyStore',{
    extend: 'Ext.data.Store',
    model: 'BSP.model.Currency',
    autoLoad: true,
    storeId: 'CurrencyStore',
    sorters: [{
        property: 'countryCode',
        direction: 'ASC'
    }]
});
