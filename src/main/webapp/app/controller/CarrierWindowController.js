Ext.define('BSP.controller.CarrierWindowController', {
	extend: 'Ext.app.ViewController',

	alias: 'controller.carrierwindow',

	onCancelButtonClick: function () {
		this.getView().close();
	},

	onSaveButtonClick: function () {
		var carrier = this.getView().getViewModel().getData().carrier;
		var store = Ext.data.StoreManager.lookup('CarrierStore');
		store.add(carrier);
		store.sync({
				success: function () {
					Ext.Msg.alert('Информация', 'Авиакомпания сохранена');
					store.reload();
				},
				failure: function(batch){
					var rawAnswer = batch.hasException()
						? batch.getExceptions()[0].getError().response.responseText
						: "{}";
					var message = Ext.decode(rawAnswer).message;
					if (!message){
						Ext.Msg.alert('Ошибка', 'Ошибка сохранения авиакомпании');
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

