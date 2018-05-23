Ext.define('BSP.store.CarrierStore', {
	extend: 'Ext.data.Store',
	model: 'BSP.model.Carrier',
	autoLoad: true,
	storeId: 'CarrierStore',
	sorters: [{
		property: 'name',
		direction: 'DESC'
	}]
});
