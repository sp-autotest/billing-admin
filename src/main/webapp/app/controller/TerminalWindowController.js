Ext.define('BSP.controller.TerminalWindowController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.terminalwindow',

    init: function () {
        var terminal = this.getView().getViewModel().get('terminal');
        var currencyIds = terminal.get('currenciesIds');
        var currencyStore = Ext.data.StoreManager.lookup('CurrencyStore');

        var store = this.lookupReference('currencyGrid').getStore();
        store.removeAll();
        Ext.each(currencyIds, function(currencyId){
            var currency = currencyStore.getById(currencyId);
            store.add(Ext.create('BSP.model.Currency', {
                id: currency.get('id'),
                countryCode: currency.get('countryCode'),
                currencyNumericCode: currency.get('currencyNumericCode')
            }));
        });
    },

    onCancelButtonClick: function () {
        var store = Ext.data.StoreManager.lookup('TerminalStore');
        store.reload();
        this.getView().close();
    },

    onSaveButtonClick: function () {
        var terminal = this.getView().getViewModel().get('terminal');
        var store =  Ext.data.StoreManager.lookup('TerminalStore');
        store.add(terminal);
        store.sync({
                success: function () {
                    Ext.Msg.alert('Информация', 'Терминал сохранен');
                    store.reload();
                },
                failure: function (batch) {
                    var rawAnswer = batch.hasException()
                        ? batch.getExceptions()[0].getError().response.responseText
                        : "{}";
                    var message = Ext.decode(rawAnswer).message;
                    if (!message){
                        Ext.Msg.alert('Ошибка', 'Ошибка сохранения терминала');
                        return;
                    }
                    Ext.Msg.alert('Ошибка', message);
                    store.reload();
                }
            }
        );
        this.getView().close();
    },

    onRemoveCurrencyButtonPressed: function (grid, rowIndex) {
        var store = grid.getStore();
        store.removeAt(rowIndex);
        grid.refresh();
        var currenciesIds = [];
        store.each(function (currency) {
            currenciesIds.push(currency.get('id'));
        });
        this.getView().getViewModel().get('terminal').set('currenciesIds', currenciesIds);
    },

    onAddCurrencyButtonPressed: function () {
        Ext.create(
            'BSP.view.carrier.AddCurrencyWindow',
            {
                viewModel: {
                    data: {
                        terminal: this.getView().getViewModel().get('terminal')
                    }
                }
            }
        ).show();
    }
});

