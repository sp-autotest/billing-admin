Ext.define('BSP.controller.AddCurrencyWindowController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.addcurrencywindow',

    init: function () {
        var comboBox = this.lookupReference('currencyCombo');
        var firstCurrencyId = comboBox.getStore().getAt(0).get('id');
        this.getView().getViewModel().set('currencyId', firstCurrencyId);
    },

    onCancelButtonClick: function () {
        this.getView().close();
    },

    onAddCurrencyButtonClick: function () {
        var terminal = this.getView().getViewModel().get('terminal');
        var currencyId = this.getView().getViewModel().get('currencyId');
        var currenciesIds = Ext.clone(terminal.get('currenciesIds'));
        if (currenciesIds.indexOf(currencyId) < 0) {
            currenciesIds.push(currencyId);
            terminal.set('currenciesIds', currenciesIds);

            var currency = this.lookupReference('currencyCombo').getStore().getById(currencyId);
            var terminalWindowCurrencyGrid = Ext.getCmp('currencyGrid');
            terminalWindowCurrencyGrid.getStore().add(Ext.create('BSP.model.Currency', {
                id: currency.get('id'),
                countryCode: currency.get('countryCode'),
                currencyNumericCode: currency.get('currencyNumericCode')
            }));
        }
        this.getView().close();
    }
});