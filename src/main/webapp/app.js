Ext.application({
    name: 'BSP',

    controllers: [
    ],

    models: [
        'Billing'
    ],

    stores: [
        'BillingStore',
        'BoStore',
        'TicketStore',
        'UserStore',
        'SystemStore',
        'CarrierStore',
        'BillingSystemStore',
        'TerminalStore',
        'CurrencyStore',
        'TerminalWindowCurrencyStore',
        'SystemSettingStore'
    ],

    views: [
        'billing.Billing',
        'bo.Bo'
    ],

    launch: function() {
        Ext.create('BSP.view.main.Main');
    }
});
