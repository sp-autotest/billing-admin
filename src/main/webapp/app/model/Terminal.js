Ext.define('BSP.model.Terminal', {
	extend: 'Ext.data.Model',

	requires: [
		'Ext.data.proxy.Rest'
	],
	
	idProperty: 'id',
	fields: [
		{
			type: 'number',
			persist: false,
			name: 'id'
		},
		{
			type: 'string',
			name: 'name'
		},
		{
			type: 'string',
			name: 'agrn'
		},
		{
			type: 'string',
			name: 'terminal'
		},
		{
			name: 'country'
		},
		{
			name: 'currenciesIds',
            defaultValue: []
		}
	],

	proxy: {
		type: 'rest',
		url: 'mvc/terminal',
		reader: {
			type: 'json',
			rootProperty: 'data'
		}
	}
});
