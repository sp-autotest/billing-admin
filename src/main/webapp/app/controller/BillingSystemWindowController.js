Ext.define('BSP.controller.BillingSystemWindowController', {
	extend: 'Ext.app.ViewController',

	alias: 'controller.bswindow',

	onCancelButtonClick: function () {
		this.getView().close();
	},

	onSaveButtonClick: function () {
		var bs = this.getView().getViewModel().getData().bs;
		var store = Ext.data.StoreManager.lookup('BillingSystemStore');
		store.add(bs);
		store.sync({
				success: function () {
					Ext.Msg.alert('Информация', 'Биллинговая система сохранена');
					store.reload();
                    Ext.data.StoreManager.lookup('CarrierStore').reload();
				},
				failure: function(batch){
					var rawAnswer = batch.hasException()
						? batch.getExceptions()[0].getError().response.responseText
						: "{}";
					var message = Ext.decode(rawAnswer).message;
					if (!message){
						Ext.Msg.alert('Ошибка', 'Ошибка сохранения биллинговой системы');
						return;
					}
					Ext.Msg.alert('Ошибка', message);
					store.reload();
				}
			}
		);
		this.getView().close();
	}
});

