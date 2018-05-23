Ext.define('BSP.view.ticket.TicketController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.ticket',

    onFindButton: function() {
        var ticketFindForm = this.lookupReference('ticketFindForm');
        var tree = this.lookupReference('ticketTree');
        var rootNode = tree.getRootNode();
        var viewModel = this.getViewModel();

        if (  ticketFindForm.isValid() ) {
            ticketFindForm.submit({
                waitMsg: 'Выполняется поиск',
                success: function(fp,o) {
                    rootNode.removeAll();
                    rootNode.appendChild(o.result.children);
                    tree.setTitle(viewModel.data.titleTreeGridDefault + ' / ' + o.result.children.length);
                },
                failure: function(fp,o) {
                    Ext.Msg.alert('Ошибка', 'Поисковый запрос вернул ошибку');
                }
            });
        }
    },

    onResetButton: function() {
        var ticketFindForm = this.lookupReference('ticketFindForm');
        ticketFindForm.reset();
        var tree = this.lookupReference('ticketTree');
        var rootNode = tree.getRootNode();
        var viewModel = this.getViewModel();
        rootNode.removeAll();
        tree.setTitle(viewModel.data.titleTreeGridDefault);
    }

});


