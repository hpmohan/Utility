//--- Remove selected job reason from picklist
var laborTypeList = component.get("v.laborTypeList");
var newLaborTypeList = laborTypeList.filter(function(a){
    if(a !== selectedLaborType){
        return a
    }
});
component.set("v.laborTypeList", newLaborTypeList);
