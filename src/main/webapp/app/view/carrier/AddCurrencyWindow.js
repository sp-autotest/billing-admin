Ext.define('BSP.view.carrier.AddCurrencyWindow', {
		extend: 'Ext.window.Window',
		alias: ['widget.addcurrencywindow'],
		requires: [
			'BSP.controller.AddCurrencyWindowController'
		],

		controller: 'addcurrencywindow',
		title: 'Валюта',
		modal: true,
		maximizable: false,
		closable: false,
		items: [
            {
                xtype: 'combobox',
                reference: 'currencyCombo',
				store: 'CurrencyStore',
                valueField: 'id',
				tpl: Ext.create('Ext.XTemplate',
					'<tpl for=".">',
					'<div class="x-boundlist-item">{countryCode} - {currencyNumericCode}</div>',
					'</tpl>'
				),
				displayTpl: Ext.create('Ext.XTemplate',
					'<tpl for=".">',
					'{countryCode} - {currencyNumericCode}',
					'</tpl>'
				),
                bind: '{currencyId}'
            }
		],
        buttons: [
            {
                listeners: {
                    click: 'onAddCurrencyButtonClick'
                },
                text: 'Добавить'
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