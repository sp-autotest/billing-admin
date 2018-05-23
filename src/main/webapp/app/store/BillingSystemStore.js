Ext.define('BSP.store.BillingSystemStore', {
	extend: 'Ext.data.Store',
	model: 'BSP.model.BillingSystem',
	autoLoad: true,
	storeId: 'BillingSystemStore',
	sorters: [{
		property: 'name',
		direction: 'DESC'
	}]
});
