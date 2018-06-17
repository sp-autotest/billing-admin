Ext.define('BSP.view.automate.AutomateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.automate',

    // Глобальный статус автоматизации
    getGlobalStatus: function () {
        Ext.Ajax.request({
            url: 'mvc/automate/status',
            success: function (response, options) {
                Ext.getCmp('currentAutomateStatus').setValue(response.responseText);
            },
            failure: function (response, options) {
                console.log('error');
            }
        });
    },

////////////////////////////////       BSP Scheduling plan     ///////////////////////////////////////////////

    onRefreshBspPlanClick: function () {
        Ext.Ajax.request({
            url: 'mvc/automate/schedulingPlanBsp',
            success: function (response, options) {
                var sched = Ext.JSON.decode(response.responseText);
                Ext.getCmp('cronExpressionBSP').setValue(sched.schedulingCronExpression);
                Ext.getCmp('nextExecutionTimeBSP').setValue(sched.nextExecutionTime);
            },
            failure: function (response, options) {
                console.log('error');
            }, scope: this
        });
    },
    onSaveBspPlanClick: function () {
        var interval = Ext.getCmp('cronExpressionBSP').getValue();
        var body = {"schedulingCronExpression": interval};

        Ext.Ajax.request({
            url: 'mvc/automate/schedulingPlanBsp',
            jsonData: body,
            method: 'POST',
            success: function (response, options) {
                this.onRefreshBspPlanClick();
                if (response.responseText !== 'ok')
                    Ext.Msg.alert('Ошибка', 'План не сохранен: ' + response.responseText);
                else
                    Ext.Msg.alert('Информация', 'План сохранен');
            },
            failure: function (response, options) {
                console.log('error');
            }, scope: this
        });
    },

////////////////////////////////       BSP task     ///////////////////////////////////////////////

    onRefreshBspTaskClick: function () {
        Ext.Ajax.request({
            url: 'mvc/automate/bspTask',
            success: function (response, options) {
                Ext.getCmp('currentTaskStatusBSP').setValue(response.responseText);
            },
            failure: function (response, options) {
                console.log('error');
            }
        });
    },
    onStopBspTaskClick: function () {
        Ext.Ajax.request({
            url: 'mvc/automate/bspTask/stop',
            success: function (response, options) {
                this.onRefreshBspTaskClick();
            },
            failure: function (response, options) {
                console.log('error');
            }, scope: this
        });
    },


    ////////////////////////////////       BO Scheduling plan     ///////////////////////////////////////////////

    onRefreshBoPlanClick: function () {
        Ext.Ajax.request({
            url: 'mvc/automate/schedulingPlanBo',
            success: function (response, options) {
                var sched = Ext.JSON.decode(response.responseText);
                Ext.getCmp('cronExpressionBo').setValue(sched.schedulingCronExpression);
                Ext.getCmp('nextExecutionTimeBO').setValue(sched.nextExecutionTime);
            },
            failure: function (response, options) {
                console.log('error');
            }, scope: this
        });
    },
    onSaveBoPlanClick: function () {
        var interval = Ext.getCmp('cronExpressionBo').getValue();
        var body = {"schedulingCronExpression": interval};

        Ext.Ajax.request({
            url: 'mvc/automate/schedulingPlanBo',
            jsonData: body,
            method: 'POST',
            success: function (response, options) {
                this.onRefreshBoPlanClick();
                if (response.responseText !== 'ok')
                    Ext.Msg.alert('Ошибка', 'План не сохранен: ' + response.responseText);
                else
                    Ext.Msg.alert('Информация', 'План сохранен');
            },
            failure: function (response, options) {
                console.log('error');
            }, scope: this
        });
    },

////////////////////////////////       BSP task     ///////////////////////////////////////////////

    onRefreshBoTaskClick: function () {
        Ext.Ajax.request({
            url: 'mvc/automate/boTask',
            success: function (response, options) {
                Ext.getCmp('currentTaskStatusBo').setValue(response.responseText);
            },
            failure: function (response, options) {
                console.log('error');
            }
        });
    },
    onStopBoTaskClick: function () {
        Ext.Ajax.request({
            url: 'mvc/automate/boTask/stop',
            success: function (response, options) {
                this.onRefreshBoTaskClick();
            },
            failure: function (response, options) {
                console.log('error');
            }, scope: this
        });
    },

    /////////////////////////////////////// server params ////////////////////////
    //bo

    onSaveBoServerClick: function () {
        var address = Ext.getCmp('boAddress').getValue();
        var port = Ext.getCmp('boPort').getValue();
        var path = Ext.getCmp('boPath').getValue();
        var login = Ext.getCmp('boLogin').getValue();
        var password = Ext.getCmp('boPassword').getValue();
        var body = {"address": address, "port": port, "path": path, "login": login, "password": password};

        Ext.Ajax.request({
            url: 'mvc/automate/serverParams?type=server.params.bo',
            jsonData: body,
            method: 'POST',
            success: function (response, options) {
                Ext.Msg.alert('Информация', 'Параметры сохранены');
                this.onUpdateBoServerClick();
            },
            failure: function (response, options) {
                Ext.Msg.alert('Ошибка', 'Ошибка сохранения параметров');
                console.log('Error saving parameters: '+ response);
            }, scope: this
        });
    },

    onUpdateBoServerClick: function () {
        Ext.Ajax.request({
            url: 'mvc/automate/serverParams?type=server.params.bo',
            success: function (response, options) {
                var json = Ext.JSON.decode(response.responseText);
                Ext.getCmp('boAddress').setValue(json.address);
                Ext.getCmp('boPath').setValue(json.path);
                Ext.getCmp('boLogin').setValue(json.login);
                Ext.getCmp('boPassword').setValue(json.password);
                Ext.getCmp('boPort').setValue(json.port);
            },
            failure: function (response, options) {
                console.log('error');
            }
        });
    },

    //posting

    onSavePostingServerClick: function () {
        var address = Ext.getCmp('postingAddress').getValue();
        var port = Ext.getCmp('postingPort').getValue();
        var path = Ext.getCmp('postingPath').getValue();
        var login = Ext.getCmp('postingLogin').getValue();
        var password = Ext.getCmp('postingPassword').getValue();
        var body = {"address": address, "port": port, "path": path, "login": login, "password": password};

        Ext.Ajax.request({
            url: 'mvc/automate/serverParams?type=server.params.posting',
            jsonData: body,
            method: 'POST',
            success: function (response, options) {
                Ext.Msg.alert('Информация', 'Параметры сохранены');
                this.onUpdatePostingServerClick();
            },
            failure: function (response, options) {
                console.log('Error saving parameters: '+ response);
                Ext.Msg.alert('Ошибка', 'Ошибка сохранения параметров');
            }, scope: this
        });
    },

    onUpdatePostingServerClick: function () {
        Ext.Ajax.request({
            url: 'mvc/automate/serverParams?type=server.params.posting',
            success: function (response, options) {
                var json = Ext.JSON.decode(response.responseText);
                Ext.getCmp('postingAddress').setValue(json.address);
                Ext.getCmp('postingPort').setValue(json.port);
                Ext.getCmp('postingPath').setValue(json.path);
                Ext.getCmp('postingLogin').setValue(json.login);
                Ext.getCmp('postingPassword').setValue(json.password);
            },
            failure: function (response, options) {
                console.log('error');
            }
        });
    }


});
