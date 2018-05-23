Ext.define('BSP.model.Carrier', {
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
			name: 'iataCode'
		},
		{
			type: 'date',
			name: 'createdAt'
		},
        {
            type: 'string',
            name: 'mcc'
        },
        {
            type: 'string',
            name: 'billingSystems'
        }
	],

	proxy: {
		type: 'rest',
		url: 'mvc/carrier',
		reader: {
			type: 'json',
			rootProperty: 'data'
		}
	}
});
