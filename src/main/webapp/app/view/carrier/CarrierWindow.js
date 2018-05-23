Ext.define('BSP.view.carrier.CarrierWindow', {
		extend: 'Ext.window.Window',
		alias: ['widget.carrierwindow'],
		requires: [
			'BSP.controller.CarrierWindowController'
		],
		controller: 'carrierwindow',
		title: 'Авиакомпания',
		modal: true,
		maximizable: false,
		closable: false,
		resizable: true,
		items: [
			{
				items: [
					{
						xtype: 'textfield',
						fieldLabel: 'Название',
						bind: '{carrier.name}',
                        required: true
					},
					{
						xtype: 'textfield',
						fieldLabel: 'IATA код',
						bind: '{carrier.iataCode}',
                        required: true
					},
                    {
                        xtype: 'textfield',
                        fieldLabel: 'MCC',
                        bind: '{carrier.mcc}',
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