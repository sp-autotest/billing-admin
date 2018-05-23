Ext.define('BSP.view.billing.BillingConverterResultWindow', {
    alias : 'widget.BillingConverterResultWindow',
	extend: 'Ext.window.Window',
	config: {
		billingConverterResult: null
	},
	constructor: function (cfg) {
		var ths = this;
		var accordionItems = [];
		Ext.each(cfg.billingConverterResult.billingConverterResultDtos, function (result) {
			accordionItems.push({
				title: result.billing.format + ' файл: ' + result.billing.name,
				items: ths.buildResultPanel(result)
			});
		});

		this.items = {
			xtype: 'container',
			autoScroll: true,
			layout: {
				type: 'vbox',
				align: 'stretch'
			},
			items: {
				xtype: 'panel',
				layout: {
					type: 'accordion',
					titleCollapse: false,
					animate: true,
					activeOnTop: false
				},
				items: accordionItems,   //Не протестировал, тк конвертер не работает
				renderTo: Ext.getBody()
			}
		};
		this.callParent(arguments);
	},
	buildResultPanel: function (billingConverterResult) {
		var panels = [];
		var billingPanel = Ext.create('Ext.grid.Panel', {
			store: {
				fields: ['title', {name: 'value', type: 'int'}],
				proxy: {
					type: 'memory',
					reader: {
						type: 'json'
					}
				},
				data: [
					{
						'title': 'Кол-во нефинансовых операций',
						'value': billingConverterResult.billing.notFinancialOperationCount
					},
					{'title': 'Кол-во депозитов', 'value': billingConverterResult.billing.depositCount},
					{'title': 'Кол-во рефандов', 'value': billingConverterResult.billing.refundCount},
					{'title': 'Кол-во реверсов', 'value': billingConverterResult.billing.reverseCount},
					{
						'title': 'Все записи без учёта нефин. операций',
						'value': billingConverterResult.billing.allRecordWithoutNotFinancialOperationCount
					}
				]
			},
			columns: [
				{text: 'Результат', dataIndex: 'title', flex: 2},
				{text: 'Количество', dataIndex: 'value', flex: 1}
			]
		});
		panels.push(billingPanel);

		Ext.each(billingConverterResult.postings, function (posting) {
			var postingPanel = Ext.create('Ext.grid.Panel', {
				title: 'Постинг файл: ' + posting.name,
				store: {
					fields: ['title', {name: 'value', type: 'int'}],
					proxy: {
						type: 'memory',
						reader: {
							type: 'json'
						}
					},
					data: [
						{'title': 'Кол-во депозитов', 'value': posting.depositCount},
						{'title': 'Кол-во рефандов', 'value': posting.refundCount},
						{'title': 'Всего записей', 'value': posting.allRecordCount}
					]
				},
				columns: [
					{text: 'Результат', dataIndex: 'title', flex: 2},
					{text: 'Количество', dataIndex: 'value', flex: 1}
				]
			});
			panels.push(postingPanel);
		});

		var dataAlreadyHandledRecords = [];
		Ext.iterate(billingConverterResult.alreadyHandledRecords, function (key, value) {
			dataAlreadyHandledRecords.push({'title': key, 'value': value});
		});
		var alreadyHandledRecordsPanel = Ext.create('Ext.grid.Panel', {
			title: 'Ранее обработанные записи',
			store: {
				fields: ['title', {name: 'value', type: 'int'}],
				proxy: {
					type: 'memory',
					reader: {
						type: 'json'
					}
				},
				data: dataAlreadyHandledRecords
			},
			columns: [
				{text: 'Имя файла', dataIndex: 'title', flex: 2},
				{text: 'Количество', dataIndex: 'value', flex: 1}
			]
		});
		panels.push(alreadyHandledRecordsPanel);

		var additionalParamsPanel = Ext.create('Ext.grid.Panel', {
			title: 'Дополнительные параметры обработки',
			store: {
				fields: ['title', {name: 'value'}],
				proxy: {
					type: 'memory',
					reader: {
						type: 'json'
					}
				},
				data: [
					{
						title: 'Кол-во ошибок при добавлении в БД',
						'value': billingConverterResult.errorAddedToDatabaseRecordsCount
					},
					{title: 'Внутрення ошибка конвертации', value: billingConverterResult.innerErrorMessage}
				]
			},
			columns: [
				{text: 'Параметр', dataIndex: 'title', flex: 2},
				{text: 'Значение', dataIndex: 'value', flex: 1}
			]
		});
		panels.push(additionalParamsPanel);
		return panels;
	},
	modal: true,
	title: 'Результат конвертации',
	height: 410,
	width: 600,
	reference: 'billingConverterResultWindow',
	layout: 'fit',
	closeable: 'true',
	autoScroll: true


});
