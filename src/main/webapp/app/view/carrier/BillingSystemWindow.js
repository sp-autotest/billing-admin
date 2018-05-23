Ext.define('BSP.view.carrier.BillingSystemWindow', {
		extend: 'Ext.window.Window',
		alias: ['widget.billingSystemWindow'],
		requires: [
			'BSP.controller.BillingSystemWindowController'
		],
		controller: 'bswindow',
		title: 'Биллинговая система',
		modal: true,
		width: '50%',
		maximizable: false,
		closable: false,
		resizable: true,
		items: [
			{
				items: [
					{
						xtype: 'textfield',
						fieldLabel: 'Название',
						bind: '{bs.name}',
                        required: true
					},
					{
                        xtype: 'combobox',
                        fieldLabel: 'Авиакомпания',
                        store: 'CarrierStore',
                        displayField: 'name',
                        valueField: 'id',
                        bind: '{bs.carrierId}',
                        required: true
					},
					{
						xtype: 'textfield',
						fieldLabel: 'Адрес хоста',
						bind: '{bs.host}',
                        required: true
					},{
						xtype: 'textfield',
						fieldLabel: 'Порт',
						bind: '{bs.port}',
                        required: true
					},
					{
						xtype: 'textfield',
						fieldLabel: 'Путь',
						bind: '{bs.path}',
                        required: true
					},
					{
						xtype: 'textfield',
						fieldLabel: 'Логин',
						bind: '{bs.login}',
                        required: true
					},
					{
						xtype: 'textfield',
						fieldLabel: 'Пароль',
						bind: '{bs.password}',
                        required: true
					},
					{
						xtype: 'textfield',
						fieldLabel: 'E-mails',
                        width: '95%',
                        bind: '{bs.emailsCSV}',
                        required: true
					},
					{
						xtype: 'textfield',
						fieldLabel: 'Маска',
						width: '95%',
						bind: '{bs.maskRegexp}',
                        required: true
					},
					{
						xtype: 'checkboxfield',
						fieldLabel: 'Включена',
						width: '95%',
						bind: '{bs.enabled}',
                        required: true
					}
				]
			}
		],
		buttons: [
			{
				listeners: {
					click: 'onSaveButtonClick'
				},
				text: 'Сохранить'
			},
			{
				listeners: {
					click: 'onCancelButtonClick'
				},
				text: 'Закрыть'
			}
		]
	}
);