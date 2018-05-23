Ext.define('BSP.view.carrier.TerminalWindow', {
		extend: 'Ext.window.Window',
		alias: ['widget.terminalwindow'],
		requires: [
			'BSP.controller.TerminalWindowController'
		],

		controller: 'terminalwindow',
		title: 'Терминал',
		width: 500,
		height: 400,
		modal: true,
		maximizable: false,
		closable: false,
		resizable: true,
		items: [
			{
				xtype: 'form',
				itemId: 'form',
				bodyPadding: 5,
				items: [
					{
						xtype: 'textfield',
						fieldLabel: 'Название',
						bind: '{terminal.name}',
                        required: true
					},
					{
                        xtype: 'combobox',
						fieldLabel: 'Авиакомпания',
                        store: 'CarrierStore',
                        displayField: 'name',
                        valueField: 'id',
                        bind: '{terminal.carrierId}',
                        required: true
					},
					{
						xtype: 'textfield',
						fieldLabel: 'AGRN',
						bind: '{terminal.agrn}',
                        required: true
					},
					{
						xtype: 'textfield',
						fieldLabel: 'Терминал',
						bind: '{terminal.terminal}',
                        required: true
					}
				]
			},
			{
				xtype: 'gridpanel',
				id: 'currencyGrid',
				reference: 'currencyGrid',
				title: 'Список валют',
				store: 'TerminalWindowCurrencyStore',
				columnLines: true,
                required: true,
				tools: [
					{
						type: 'plus',
						tooltip: 'Добавить валюту',
						handler: 'onAddCurrencyButtonPressed'
					}
				],
				columns: [
					{
						xtype: 'gridcolumn',
						flex: 1,
						text: 'Буквенный код',
						dataIndex: 'countryCode'
					},
					{
						xtype: 'gridcolumn',
						flex: 1,
						text: 'Цифровой код',
						dataIndex: 'currencyNumericCode'
					},
					{
						xtype: 'actioncolumn',
						width: 30,
						items: [
							{
								icon: 'resources/img/minus.png',
								tooltip: 'Удалить валюту',
								handler: 'onRemoveCurrencyButtonPressed'
							}
						]
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