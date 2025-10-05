var date = new Date();
var d = date.getDate();
var m = date.getMonth();
var y = date.getFullYear();
var date1 = new Date();
var d1 = date.getDate();
var m1 = date.getMonth();
var y1 = date.getFullYear();
var meses = new Array ("01","02","03","04","05","06","07","08","09","10","11","12");

$(document).ready(function() {
	$(document).bind('contextmenu',function(e){
			//return false;
	});

	 $('#docs pre code').each(function(){
	    var $this = $(this);
	    var t = $this.html();
	    $this.html(t.replace(/</g, '&lt;').replace(/>/g, '&gt;'));
	});

	function getRandomInt(min, max) {
	  return Math.floor(Math.random() * (max - min + 1)) + min;
	};

	$(document).on('click', '.the-icons a', function(e){
		e && e.preventDefault();
	});

	$(document).on('change', 'table thead [type="checkbox"]', function(e){
		e && e.preventDefault();
		var $table = $(e.target).closest('table'), $checked = $(e.target).is(':checked');
		$('tbody [type="checkbox"]', $table).prop('checked', $checked);
		console.log($checked);
	});

	$(document).on('click', '[data-toggle^="progress"]', function(e){
		e && e.preventDefault();

		$el = $(e.target);
		$target = $($el.data('target'));
		$('.progress', $target).each(
			function(){
				var $max = 50, $data, $ps = $('.progress-bar',this).last();
				($(this).hasClass('progress-xs') || $(this).hasClass('progress-sm')) && ($max = 100);
				$data = Math.floor(Math.random()*$max)+'%';
				$ps.css('width', $data).attr('data-original-title', $data);
			}
		);
	});
	
	function addNotification($notes){
		var $el = $('.nav-msg'), $n = $('.count-n:first', $el), $item = $('.list-group-item:first', $el).clone(), $v = parseInt($n.text());
		$('.count-n', $el).fadeOut().fadeIn().text($v+1);
		$item.attr('href', $notes.link);
		$item.find('.pull-left').html($notes.icon);
		$item.find('.media-body').html($notes.title);
		$item.hide().prependTo($el.find('.list-group')).slideDown().css('display','block');
	}

	// fullcalendar
	
	var addDragEvent = function($this){
		// create an Event Object (http://arshaw.com/fullcalendar/docs/event_data/Event_Object/)
		// it doesn't need to have a start or end
		var eventObject = {
			title: $.trim($this.text()), // use the element's text as the event title
			className: $this.attr('class').replace('label','')
		};
		
		// store the Event Object in the DOM element so we can get to it later
		$this.data('eventObject', eventObject);
		
		// make the event draggable using jQuery UI
		$this.draggable({
			zIndex: 999,
			revert: true,      // will cause the event to go back to its
			revertDuration: 0,  //  original position after the drag
			

		});
	};
	$('.calendar').each(function() {
		$(this).fullCalendar({
			
			header: {
				left: 'prev,next',
				center: 'title',
				right: 'today,month,agendaWeek,agendaDay'
			},
			defaultView: 'agendaWeek',
			slotMinutes: 15,
			firstHour: 9, // a start time (9am in this example)
            lasthour: 18, // an end time (6pm in this example)
            dow: [ 1, 2, 3, 4 , 5],
			hiddenDays: [ 0 ],
			firstDay: 1,
			slotEventOverlap: false,
			editable: true,
			eventDrop: function(event, delta, revertFunc) {

				date = event.start;
				d = date.getDate();
				m = date.getMonth();
				y = date.getFullYear();

				date1 = event.end;
				d1 = date1.getDate();
				m1 = date1.getMonth();
				y1 = date1.getFullYear();

		        $.ajax({
                  url: 'agenda-g.php',
                  type: 'post',
                  data: {id: event.id, accion: 'edit', fecini: y+"-"+meses[date.getMonth()]+"-"+d, fecfin: y1+"-"+meses[date1.getMonth()]+"-"+d1, horini: date.getHours()+":"+date.getMinutes()+":"+date.getSeconds(), horfin: date1.getHours()+":"+date1.getMinutes()+":"+date1.getSeconds(), idpac: '', tipo: ''},
                  dataType: 'json',
                  success: function (data) {
                    //console.log(data);

                   }
                });


		    },
		    viewRender: function(view, element) {
                console.log(view);
                date = view.start;
				d = date.getDate();
				m = date.getMonth();
				y = date.getFullYear();

				date1 = view.end;
				d1 = date1.getDate();
				m1 = date1.getMonth();
				y1 = date1.getFullYear();

                $.ajax({
	                url: 'agenda-b.php',
	                type: 'post',
	                data: {fecini: y+"-"+meses[date.getMonth()]+"-"+d, fecfin: y1+"-"+meses[date1.getMonth()]+"-"+d1, fecfin: y1+"-"+meses[date1.getMonth()]+"-"+d1},
	                dataType: 'json',
	                success: function (data) {
	                  console.log(data);
	                  var consulta = 0;
	                  var estudio = 0;
	                  var resultado = 0;
	                  $('#badge-c').html(consulta);
	                  $('#badge-e').html(estudio);
	                  $('#badge-r').html(resultado);

	                  $('#calendar').fullCalendar( 'removeEvents');
	                  $.each(data, function(indice, evento) {
	                    if(evento['className']=='bg-success'){ consulta++;  $('#badge-c').html(consulta);}
	                    if(evento['className']=='bg-primary'){ estudio++;  $('#badge-e').html(estudio);}
	                    if(evento['className']=='bg-info'){ resultado++;  $('#badge-r').html(resultado);}

	                    $('#calendar').fullCalendar( 'addEventSource', [{
	                      id: evento['id'],
	                      title: evento['title'],
	                      start  : evento['start'],
	                      end    : evento['end'],
	                      allDay: false,
	                      className:evento['className']
	                    }] );
	                  });
	                }
	            });
            },
			droppable: true, // this allows things to be dropped onto the calendar !!!
			drop: function(date, allDay) { // this function is called when something is dropped
					alert("Dropped on " + date.format());
					// retrieve the dropped element's stored Event Object
					var originalEventObject = $(this).data('eventObject');
					
					// we need to copy it, so that multiple events don't have a reference to the same object
					var copiedEventObject = $.extend({}, originalEventObject);
					
					// assign it the date that was reported
					copiedEventObject.start = date;
					copiedEventObject.allDay = allDay;
					alert('aqui');
					// render the event on the calendar
					// the last `true` argument determines if the event "sticks" (http://arshaw.com/fullcalendar/docs/event_rendering/renderEvent/)
					$('#calendar').fullCalendar('renderEvent', copiedEventObject, true);
					
					// is the "remove after drop" checkbox checked?
					if ($('#drop-remove').is(':checked')) {
						// if so, remove the element from the "Draggable Events" list
						$(this).remove();
					}
					
				}
			,
			eventResize: function(event, delta, revertFunc) {

		        date = event.start;
				d = date.getDate();
				m = date.getMonth();
				y = date.getFullYear();

				date1 = event.end;
				d1 = date1.getDate();
				m1 = date1.getMonth();
				y1 = date1.getFullYear();

		        $.ajax({
                  url: 'agenda-g.php',
                  type: 'post',
                  data: {id: event.id, accion: 'edit', fecini: y+"-"+meses[date.getMonth()]+"-"+d, fecfin: y1+"-"+meses[date1.getMonth()]+"-"+d1, horini: date.getHours()+":"+date.getMinutes()+":"+date.getSeconds(), horfin: date1.getHours()+":"+date1.getMinutes()+":"+date1.getSeconds(), idpac: '', tipo: ''},
                  dataType: 'json',
                  success: function (data) {
                    //console.log(data);

                   }
                });

		        
		    },
			eventClick: function(event, element) {

			    //event.title = "CLICKED!";
		        //$('#calendar').fullCalendar('updateEvent', event);
		        $('.menu-contex').show();
		        ida = event.id;
		        cambiotamano();

		    },

		    events: [
				
			]
		});
	});
	$('#myEvents').on('change', function(e, item){
		addDragEvent($(item));
		
	});

	
	
	$('#myEvents li').each(function() {
		addDragEvent($(this));

	});

	

		

	// fuelux datagrid
	var DataGridDataSource = function (options) {
		this._formatter = options.formatter;
		this._columns = options.columns;
		this._delay = options.delay;
	};

	DataGridDataSource.prototype = {

		columns: function () {
			return this._columns;
		},

		data: function (options, callback) {
			var url = '/js/data/datagrid.json';
			var self = this;

			setTimeout(function () {

				var data = $.extend(true, [], self._data);

				$.ajax(url, {
					dataType: 'json',
					async: false,
					type: 'GET'
				}).done(function (response) {

					data = response.staff;
					// SEARCHING
					if (options.search) {
						data = _.filter(data, function (item) {
							var match = false;

							_.each(item, function (prop) {
								if (_.isString(prop) || _.isFinite(prop)) {
									if (prop.toString().toLowerCase().indexOf(options.search.toLowerCase()) !== -1) match = true;
								}
							});

							return match;
						});
					}

					// FILTERING
					// if (options.filter) {
					// 	data = _.filter(data, function (item) {
					// 		switch(options.filter.value) {
					// 			case 'lt5m':
					// 				if(item.population < 5000000) return true;
					// 				break;
					// 			case 'gte5m':
					// 				if(item.population >= 5000000) return true;
					// 				break;
					// 			default:
					// 				return true;
					// 				break;
					// 		}
					// 	});
					// }

					var count = data.length;

					// SORTING
					if (options.sortProperty) {
						data = _.sortBy(data, options.sortProperty);
						if (options.sortDirection === 'desc') data.reverse();
					}

					// PAGING
					var startIndex = options.pageIndex * options.pageSize;
					var endIndex = startIndex + options.pageSize;
					var end = (endIndex > count) ? count : endIndex;
					var pages = Math.ceil(count / options.pageSize);
					var page = options.pageIndex + 1;
					var start = startIndex + 1;

					data = data.slice(startIndex, endIndex);

					if (self._formatter) self._formatter(data);

					callback({ data: data, start: start, end: end, count: count, pages: pages, page: page });
				}).fail(function(e){

				});
			}, self._delay);
		}
	};

    $('#MyStretchGrid').each(function() {
    	$(this).datagrid({
	        dataSource: new DataGridDataSource({
			    // Column definitions for Datagrid
			    columns: [
					{
						property: 'name',
						label: 'Name',
						sortable: true
					},
					{
						property: 'title',
						label: 'Title',
						sortable: true
					},
					{
						property: 'email',
						label: 'Email',
						sortable: true
					},
					{
						property: 'phone',
						label: 'Phone',
						sortable: true
					},
					{
						property: 'user_id',
						label: 'Edit',
						sortable: true
					}
				],

			    // Create IMG tag for each returned image
			    formatter: function (items) {
			      $.each(items, function (index, item) {
			        item.name = '<a href="#" class="thumb-xs pull-left m-r-sm"><img src="/images/avatar.jpg" class="img-circle"></a><a href="#edit?user='+item.user_id+'">'+ item.name +'</a>'
			        item.user_id = '<a href="#edit?user='+item.user_id+'"><i class="fa fa-pencil"></i></a>';
			      });
			    }
		  })
	    });
	});

	// datatable
	$('[data-ride="datatables"]').each(function() {
		var oTable = $(this).dataTable( {
			"bProcessing": true,
			"sAjaxSource": "js/data/datatable.json",
			"sDom": "<'row'<'col-sm-6'l><'col-sm-6'f>r>t<'row'<'col-sm-6'i><'col-sm-6'p>>",
			"sPaginationType": "full_numbers",
			"aoColumns": [
				{ "mData": "engine" },
				{ "mData": "browser" },
				{ "mData": "platform" },
				{ "mData": "version" },
				{ "mData": "grade" }
			]
		} );
	});

	// select2 
   	if ($.fn.select2) {
        $("#select2-option").select2();
        $("#select2-tags").select2({
          tags:["red", "green", "blue"],
          tokenSeparators: [",", " "]}
        );
    }

	// Morris
	$('a[href="#morris"]').on('shown.bs.tab', function(){
		if($('.graph').children().length > 0) return;
		buildMorris(false);
		var morrisResize;
		$(window).resize(function(e) {
			clearTimeout(morrisResize);
			morrisResize = setTimeout(function(){buildMorris(true)}, 500);
		});
	});

	var buildMorris = function($re){
		if($re){
			$('.graph').html('');
		}
		var tax_data = [
	       {"period": "2011 Q3", "licensed": 3407, "sorned": 660},
	       {"period": "2011 Q2", "licensed": 3351, "sorned": 629},
	       {"period": "2011 Q1", "licensed": 3269, "sorned": 618},
	       {"period": "2010 Q4", "licensed": 3246, "sorned": 661},
	       {"period": "2009 Q4", "licensed": 3171, "sorned": 676},
	       {"period": "2008 Q4", "licensed": 3155, "sorned": 681},
	       {"period": "2007 Q4", "licensed": 3226, "sorned": 620},
	       {"period": "2006 Q4", "licensed": 3245, "sorned": null},
	       {"period": "2005 Q4", "licensed": 3289, "sorned": null}
		];
		Morris.Line({
			element: 'hero-graph',
			data: tax_data,
			xkey: 'period',
			ykeys: ['licensed', 'sorned'],
			labels: ['Licensed', 'Off the road'],
			lineColors: ['#59dbbf', '#aeb6cb']
		});

		Morris.Donut({
			element: 'hero-donut',
			data: [
			  {label: 'Jam', value: 25 },
			  {label: 'Frosted', value: 40 },
			  {label: 'Custard', value: 25 },
			  {label: 'Sugar', value: 10 }
			],
			colors:['#afcf6f'],
			formatter: function (y) { return y + "%" }
		});

		buildArea();

		Morris.Bar({
			element: 'hero-bar',
			data: [
			  {device: 'iPhone', geekbench: 136},
			  {device: 'iPhone 3G', geekbench: 137},
			  {device: 'iPhone 3GS', geekbench: 275},
			  {device: 'iPhone 4', geekbench: 380},
			  {device: 'iPhone 4S', geekbench: 655},
			  {device: 'iPhone 5', geekbench: 1571}
			],
			xkey: 'device',
			ykeys: ['geekbench'],
			labels: ['Geekbench'],
			barRatio: 0.4,
			xLabelAngle: 35,
			hideHover: 'auto',
			barColors:['#aeb6cb']
		});
	};

	var buildArea = function(){
		Morris.Area({
			element: 'hero-area',
			data: [
			  {period: '2010 Q1', iphone: 2666, ipad: null, itouch: 2647},
			  {period: '2010 Q2', iphone: 2778, ipad: 2294, itouch: 2441},
			  {period: '2010 Q3', iphone: 4912, ipad: 1969, itouch: 2501},
			  {period: '2010 Q4', iphone: 3767, ipad: 3597, itouch: 5689},
			  {period: '2011 Q1', iphone: 6810, ipad: 1914, itouch: 2293},
			  {period: '2011 Q2', iphone: 5670, ipad: 4293, itouch: 1881},
			  {period: '2011 Q3', iphone: 4820, ipad: 3795, itouch: 1588},
			  {period: '2011 Q4', iphone: 15073, ipad: 5967, itouch: 5175},
			  {period: '2012 Q1', iphone: 10687, ipad: 4460, itouch: 2028},
			  {period: '2012 Q2', iphone: 8432, ipad: 5713, itouch: 1791}
			],
			xkey: 'period',
			ykeys: ['iphone', 'ipad', 'itouch'],
			labels: ['iPhone', 'iPad', 'iPod Touch'],			
			hideHover: 'auto',
			lineWidth: 2,
			pointSize: 4,
			lineColors: ['#59dbbf', '#aeb6cb', '#5dcff3'],
			fillOpacity: 0.5,
			smooth: true,
		});
	};

	$('#tab1 #hero-area').each(function(){
		buildArea();
		var morrisResizes;
		$(window).resize(function(e) {
			clearTimeout(morrisResizes);
			morrisResizes = setTimeout(function(){
				$('.graph').html('');
				buildArea();
			}, 500);
		});
	});




});