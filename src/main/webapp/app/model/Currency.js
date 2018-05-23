Ext.define('BSP.model.Currency', {
	extend: 'Ext.data.Model',

	requires: [
		'Ext.data.proxy.Rest'
	],
	
	idProperty: 'id',
	fields: [
		{
			type: 'number',
			name: 'id'
		},
		{
			type: 'string',
			name: 'countryCode'
		},
		{
			type: 'string',
			name: 'currencyNumericCode'
		}
	],

	proxy: {
		type: 'rest',
		api: {
			read: 'mvc/currency'
		},
		reader: {
			type: 'json',
			rootProperty: 'data'
		}
	}
});
