

Ext.apply(Ext.form.VTypes, {
  inoutidentifier: function(val, field) {

    var i;

    for(i=0; i< describe.inputs.length; i++){
        if(val == describe.inputs[i].Identifier)
            return false;
    }

    for(i=0; i< describe.outputs.length; i++){
        if(val == describe.outputs[i].Identifier)
            return false;
    }
    
    return true;
  },
  inoutidentifierText: 'This Input/Output is already defined.'
});



Ext.apply(Ext.form.VTypes, {
  minoccurs: function(val) {

    var maxOccurs=Ext.getCmp("max").getValue();
    
    return val <= maxOccurs;
  },
  minoccursText: 'Min Occours should be less or equal to Max Occours.'
});


Ext.apply(Ext.form.VTypes, {
  maxoccurs: function(val) {

    var minOccurs=Ext.getCmp("min").getValue();
    
    return val >= minOccurs;
  },
  maxoccursText: 'Max Occours should be greather or equal to Min Occours.'
});


