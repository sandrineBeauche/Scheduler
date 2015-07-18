$(function() {
	var Task = can.Model.extend({
		create: function( attrs ){
			   var result = $.ajax({url: '/scheduler/rest/api/scheduler/task',
				   	type: "POST",
				     contentType: "text/plain", 
				     dataType: "text",
				     data: attrs.scriptContent
			   });
			   return {"id": result};
		 }, 
		findAll: function(params){
			if(params.status == "finished"){
				return Task.findFinished({});
			}
			else{
				return Task.findRunning({});
			}
		},
		findRunning: function(params){
			return $.ajax({
				     url: '/scheduler/rest/api/scheduler/task/running',
				     type: 'get',
				     dataType: 'json'})
				 }, 
		findFinished: function(params){
			return $.ajax({
			     url: '/scheduler/rest/api/scheduler/task/finished',
			     type: 'get',
			     dataType: 'json'})
			 },
	    findOne: 'GET /scheduler/rest/api/scheduler/task/{id}/status',
		destroy: 'DELETE /scheduler/rest/api/scheduler/task/{id}'
	}, {});


	can.Component.extend({
		tag: "task-submit",
		viewModel:{
			saveTask: function() {
				var content = $("#scriptContentText").val();
				var task = new Task({scriptContent: content});
				task.save();
			}
		}
	});
	
	var showResultTask = function(task){
	    	Task.findOne({id: task.id}, function(result){
	    		$("#dialog").html(can.view("statusTaskDialogMustache", result));
	    		$( "#dialog" ).dialog({width: 500});
	    	});
	};
	
	can.Component.extend({
		tag: "tasks-view-finished",
		template: can.view("statusTaskViewMustache"),
		viewModel: {
		    tasks: new Task.List({status: "finished"}),
		    getResult: showResultTask
		  }
		});
	
	can.Component.extend({
		tag: "tasks-view-running",
		template: can.view("statusTaskViewMustache"),
		viewModel: {
		    tasks: new Task.List({status: "running"}),
		    getResult: showResultTask
		  }
		});

	$("#taskViews").html(can.view("tasksViewMustache", {}));
	$( "#tabs" ).tabs();
});

