Ext.define('BSP.view.billing.BillingModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.billing',

    stores: {
        //billingStore : 'BillingStore',
        billing : {
            fields:['name', 'email', 'phone'],
            //model: 'BSP.model.Profile',
            data:{'items':[
                { 'name': 'Lisa',  "email":"lisa@simpsons.com",  "phone":"555-111-1224"  },
                { 'name': 'Bart',  "email":"bart@simpsons.com",  "phone":"555-222-1234" },
                { 'name': 'Homer', "email":"homer@simpsons.com",  "phone":"555-222-1244"  },
                { 'name': 'Marge', "email":"marge@simpsons.com", "phone":"555-222-1254"  }
            ]},
            proxy: {
                type: 'memory'
//                reader: {
//                    type: 'json',
//                    rootProperty: 'items'
//                }
            }
        }
    },
    data: {
        titleTreeGridDefault: 'Биллинговые файлы'
    }

});

