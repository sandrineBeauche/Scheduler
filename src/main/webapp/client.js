$(function() {
	var Task = can.Model.extend({
		create: function( attrs ){
			   return $.ajax({url: '/scheduler/rest/api/scheduler/task',
				   	type: "POST",
				     contentType: "text/plain", 
				     dataType: "text",
				     data: attrs.scriptContent,
				     dataFilter: function(data){
				    	 return {"id": data, "createdAt": $.now()}
				     }
			   });
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
	    findOne: function(params){
	    	return $.ajax({url: '/scheduler/rest/api/scheduler/task/' + params.id + '/status',
			   	type: "GET",
			     dataType: "json",
			     dataFilter: function(data){
			    	 var objData = JSON.parse(data);
			    	 if(typeof objData.result == "object"){
			    		 objData.result = JSON.stringify(objData.result, null, 2);
			    	 }
			    	 return JSON.stringify(objData);
			     }
		   });
	    },
		destroy: 'DELETE /scheduler/rest/api/scheduler/task/{id}'
	}, {});

	
	var finishedTasks = new Task.List({status: "finished"});
	var runningTasks = new Task.List({status: "running"});

	can.Component.extend({
		tag: "task-submit",
		viewModel:{
			saveTask: function() {
				var content = $("#scriptContentText").val();
				$("#scriptContentText").val("");
				var task = new Task({scriptContent: content});
				task.save(function (savedTask){
					$("#dialog").html(can.view("messageCreatedDialogMustache", {taskId: savedTask.id}));
		    		$( "#dialog" ).dialog({width: 500, title: "Task created"});
		    		finishedTasks.replace(Task.findAll({status: "finished"}));
		    		runningTasks.replace(Task.findAll({status: "running"}));
				});
			}
		}
	});
	
	var showResultTask = function(task){
	    	Task.findOne({id: task.id}, function(result){
	    		$("#dialog").html(can.view("statusTaskDialogMustache", result));
	    		$( "#dialog" ).dialog({width: 500, title:"Status and result"});
	    	});
	};
	
	
	
	can.Component.extend({
		tag: "tasks-view-finished",
		template: can.view("statusTaskViewMustache"),
		viewModel: {
		    tasks: finishedTasks,
		    getResult: showResultTask
		  }
		});
	
	can.Component.extend({
		tag: "tasks-view-running",
		template: can.view("statusTaskViewMustache"),
		viewModel: {
		    tasks: runningTasks,
		    getResult: showResultTask
		  }
		});

	$("#taskViews").html(can.view("tasksViewMustache", {}));
	$( "#tabs" ).tabs();
});

