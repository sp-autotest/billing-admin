Ext.define('BSP.controller.CarriersController', {
	extend: 'Ext.app.ViewController',

	alias: 'controller.carriers',

	onCarrierClick: function (view, record) {
		Ext.create(
			'BSP.view.carrier.CarrierWindow',
			{
				viewModel: {
					data: {
						carrier: record
					}
				}
			}
		).show();
	},

	onBillingSystemClick: function (view, record) {
		Ext.create(
			'BSP.view.carrier.BillingSystemWindow',
			{
				viewModel: {
					data: {
						bs: record
					}
				}
			}
		).show();
	},

	onAddCarrierButtonPressed: function () {
		Ext.create(
			'BSP.view.carrier.CarrierWindow',
			{
				viewModel: {
					data: {
						carrier: Ext.create('BSP.model.Carrier')
					}
				}
			}
		).show();
	},
    onAddBillingSystemButtonPressed: function () {
		Ext.create(
			'BSP.view.carrier.BillingSystemWindow',
			{
				viewModel: {
					data: {
						bs: Ext.create('BSP.model.BillingSystem')
					}
				}
			}
		).show();
	},

	onTerminalClick: function (view, record) {
		Ext.create(
			'BSP.view.carrier.TerminalWindow',
			{
				viewModel: {
					data: {
						terminal: record
					}
				}
			}
		).show();
	},

	onAddTerminalButtonPressed: function () {
		Ext.create(
			'BSP.view.carrier.TerminalWindow',
			{
				viewModel: {
					data: {
						terminal: Ext.create('BSP.model.Terminal')
					}
				}
			}
		).show();
	},

	onFilterChanged: function () {
		var filter = this.getViewModel().get('filter');
		var store = this.lookupReference('terminalsGrid').getStore();
		store.clearFilter();
		if (filter.carrierId){
			store.addFilter({
				property: 'carrierId',
				value: filter.carrierId
			});
		}
	},

	onResetButtonPressed: function () {
		this.getViewModel().set('filter', {});
		this.onFilterChanged();
	},

	onRemoveTerminalButtonPressed: function (grid, rowIndex) {
		var terminal = grid.getStore().getAt(rowIndex);
		Ext.Msg.show({
			title: 'Подтверждение',
			message: 'Удалить терминал "' + terminal.get('name') + '"?',
			buttons: Ext.Msg.OKCANCEL,
			icon: Ext.window.MessageBox.WARNING,
			fn: function (btn) {
				if (btn != 'ok') return;

				var store = grid.getStore();
				var terminal = store.getAt(rowIndex);
				store.remove(terminal);
				store.sync({
						success: function () {
							Ext.Msg.alert('Информация', 'Терминал удален');
						},
						failure: function () {
							Ext.Msg.alert('Ошибка', 'Ошибка удаления терминала');
						}
					}
				);
			}
		});
	}
});

