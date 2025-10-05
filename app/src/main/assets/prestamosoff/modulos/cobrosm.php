<?php if (!isset($_SESSION)) { session_start(); }
if(!isset($_SESSION['user'])){
header('Location:salir.php');
}
include "../include/db.php";
include "header.php";
?>
<style type="text/css" media="screen">
	.recibo ul {
        list-style: none;
        padding: 0px;
        margin: 0px;
    }

    .recibo li {
        display: inline-block;
    }
    .font1{
    	font-size: 14px;
    }
    .font2{
    	font-size: 13px;
    }
	</style>
<link rel="stylesheet" href="../css/cobrosm.css" type="text/css" />
<section id="content">
	<section class="vbox">
		<header class="header bg-black navbar navbar-inverse">
			<div class="collapse navbar-collapse pull-in">
				<ul class="nav navbar-nav m-l-n" id="menudirect">
					<li ><a href="#">.</a></li>
					<!-- <li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="#">Action</a></li>
							<li><a href="#">Another action</a></li>
							<li><a href="#">Something else here</a></li>
							<li><a href="#">Separated link</a></li>
							<li><a href="#">One more separated link</a></li>
						</ul>
					</li> -->
				</ul>
			</div>
		</header>
		<section class="scrollable" id="pjax-container">
			<div class="wrapper">
				<div class="conten">
					<div class="tabl">
						<fieldset class="pantalla">
							<legend margin-bottom="0px" style="margin-bottom: 5px;text-align: center;font-size: x-large;color: white;width: 102%;height: 35px;background: #019bc6;background: -moz-linear-gradient(top, #019bc6 0, #017cb4 100%);background: -webkit-gradient(linear, left top, left bottom, color-stop(0, #019bc6), color-stop(100%, #017cb4));background: -o-linear-gradient(top, #019bc6 0, #017cb4 100%);background: -ms-linear-gradient(top, #019bc6 0, #017cb4 100%);background: linear-gradient(to bottom, #019bc6 0, #017cb4 100%);border-top-left-radius: 4px;border-top-right-radius: 4px;"> <font size="5">Cobros</font></legend>
							<div class="screen-home">
								<ul>
									<li id="label" class="min">
										<b>No. Cob.:</b>
									</li>
									<li class="min2">
										<div class="form-group input-group-sm">
											<input name="num_cob" id="num_cob" type="text" class="form-control" data-next='nom_cli' style="background-color: white;text-align: right;" readonly="true">
											<input type="hidden" id="numcob" value="">
										</div>
									</li>
									<li id="label" class="min1">
										<a href="#" style="color: black;font-size: 20px;" id="btn-fac"><i class="fa fa-search" aria-hidden="true" ></i></a>
									</li>
									<li id="label" class="min" >
										<b>Fecha:</b>
									</li>
									<li class="min3" >
										<div class="form-group input-group-sm">
											<input value="<?php echo $fecha; ?>" name="fec_cob" id="fec_cob" type="text" class="form-control" data-next='nom_cli' style="background-color: white;" readonly="true">
											<input type="hidden" id="feccob" value="<?php echo $fecha; ?>">
											
										</div>
									</li>									
									
								</ul>
								
								<ul id="selectizer1">
									<li id="label" class="min">
										<b>Prestamo:</b>
									</li>
									<li class="gra selectizer1" >
										<div class="prestamo-select-col">
											<div class="sel-wrap">
												<select id="pre_cob" class="selectize" placeholder="Seleccionar Prestamo">
												</select>
											</div>
										</div>
									</li>
								</ul>
								<ul>
									<li id="label" class="min">
										<b>Balance:</b>
									</li>
									<li class="min3" >
										<div class="form-group input-group-sm">
											<input name="bal_pre" id="bal_pre" type="text" class="form-control" data-next='nom_cli' readonly="true">
										</div>
									</li>	
									<li id="label" class="min" >
										<b>Monto:</b>
									</li>
									<li class="min3" >
										<div class="form-group input-group-sm">
											<input name="mon_cob" id="mon_cob" type="number" class="form-control" data-next='nom_cli' >
										</div>
									</li>	
								</ul>
								<ul>
									<li id="label" class="min">
										<b>Mora:</b>
									</li>
									<li class="min3" >
										<div class="form-group input-group-sm" >
											<select class="form-control" name="mor_cob" id="mor_cob" placeholder="CONCEPTO" style="font-weight: bold;padding: 5px 2px;    font-size: 13px;">
												
												<option value="2">NO</option>
												<option value="1">SI</option>
											</select>
										</div>
									</li>
									<li id="label" class="min" >
										<b>Dias:</b>
									</li>
									<li class="min3" >
										<div class="form-group input-group-sm">
											<input name="gra_cob" id="gra_cob" type="text" class="form-control" data-next='nom_cli' >
										</div>
									</li>	
								</ul>
								<ul>									
									<li class="gra1" >
										<div class="form-group input-group-sm">
											<input name="con_cob" id="con_cob" type="text" class="form-control" data-next='nom_cli' style="background-color: white;" readonly="true">
										</div>
									</li>	
								</ul>
								<ul>									
									<li class="gra1" >
										<div class="form-group input-group-sm">
											<input name="mon_let" id="mon_let" type="text" class="form-control" data-next='nom_cli' style="background-color: white;" readonly="true">
										</div>
									</li>	
								</ul>
								<table class="table table-striped table-hover cuota">
									<thead>
										<tr>
											<th style="padding: 0px 8px; font-weight: bold;width: 85px;">Cuo</th>
											<th style="padding: 0px 8px; font-weight: bold;width: 100px;">Fecha</th>
											<th style="padding: 0px 8px; font-weight: bold;width: 97px;">Mora</th>
											<th style="padding: 0px 8px; font-weight: bold;width: 102px;">Balance</th>
											<th style="padding: 0px 8px; font-weight: bold;width: 110px;">Pago</th>
										</tr>
									</thead>
									<tbody class="cuotas" style="height: 167px;">
										
									</tbody>
								</table>
								
								<div class="btn-group" role="group" aria-label="..." style="width: 101%;position: absolute;bottom: 46px;">
								  <button class="btn btn-md btn-default botones" id="btn-nuevo" >
										<span class="glyphicon glyphicon-file"></span>
									</button>
									<button class="btn btn-md btn-warning botones" id="btn-cancelar">
										<span>C</span>
									</button>
									<button class="btn btn-md btn-success botones" id="btn-salvar">
										<span class="glyphicon glyphicon-saved"></span>
									</button>
									<button class="btn btn-md btn-info botones" id="btn-imprimir">
										<span class="glyphicon glyphicon-print"></span>
									</button>
								</div>
								<div class="btn-group" role="group" aria-label="..." style="width: 101%;position: absolute;bottom: 0px;">
								  <button class="btn btn-md btn-default botones" id="btn-inicio" >
										<span class="glyphicon glyphicon-fast-backward"></span>
									</button>
									<button class="btn btn-md btn-default botones" id="btn-atras">
										<span class="glyphicon glyphicon-backward"></span>
									</button>
									<button class="btn btn-md btn-default botones" id="btn-adelante">
										<span class="glyphicon glyphicon-forward"></span>
									</button>
									<button class="btn btn-md btn-default botones" id="btn-fin">
										<span class="glyphicon glyphicon-fast-forward"></span>
									</button>
								</div>
								
							</div>
							<div class="msgimp" style="">
								<div class="opciones" style="margin-top: 279px;">
									<button class="btn btn-success" type="submit" id="print-8"><span class="glyphicon glyphicon-print" aria-hidden="true"></span> Printer</button>
									<button class="btn btn-info" type="submit" id="print-pv"><span class="glyphicon glyphicon-picture" aria-hidden="true"></span> Imagen</button>
									<button class="btn btn-danger" type="submit" id="print-ca"><span class="glyphicon glyphicon-log-out" aria-hidden="true"></span> Cancelar</button>
								</div>
							</div>
							<a href="images/fondo.jpg" download="fondo.jpg" id="desca"></a>
							<div class="recibo crear" style="background: white;width: 320px;max-width: 320px;min-width: 320px;">
								<ul style="text-align: center;font-weight: bold;" class="font1">
									<li id=""><img src="/images/logorec.jpeg" alt="" style="max-width: 320px;"></li>
								</ul>
								<ul style="text-align: center;" class="font2">
									<li id="lidir_sys">C/ ALEGRIA # 3, CENTRO CIUDAD, SANTIAGO</li>
								</ul>
								<ul style="text-align: center;" class="font2">
									<li id="litel_sys">829-815-6619 / 829-257-6619</li>
								</ul>
								<br>
								<ul style="margin-bottom: 5px;text-align: center;font-weight: bold; border-bottom: 1px dashed;" class="font1">
									<li>COMPROBANTE DE PAGO</li>
								</ul>
								<ul style="text-align: left;" class="font2">
									<li style="font-weight: bold;">No. Recibo:</li>
									<li id="linum_cob">0005486</li>
								</ul>
								<ul style="text-align: left;" class="font2">
									<li style="font-weight: bold;">Cliente: </li>
									<li id="linom_cli">ALEXANDER MIGUEL LOPEZ RODRIGUEZ</li>
								</ul>
								<ul style="text-align: left;" class="font2">
									<li style="font-weight: bold;">Prestamo:</li>
									<li id="lipre_cob">000015</li>
								</ul>
								<ul style="text-align: left;" class="font2">
									<li style="font-weight: bold;">Fecha: </li>
									<li id="lifec_cob">12/02/2018</li>
								</ul>
								<ul style="text-align: left;border-bottom: 1px dashed;margin-bottom: 5px;" class="font2">
									<li style="font-weight: bold;">Cajero:</li>
									<li id="liusu_cob">ALEXANDER LOPEZ</li>
								</ul>
								<ul style="text-align: center;border-bottom: 1px dashed;margin-bottom: 5px;" class="font2">
									<li style="font-weight: bold;width:20%">Cuota</li>
									<li style="font-weight: bold;width:36%">Monto</li>
									<li style="font-weight: bold;width:36%">Balance</li>
								</ul>
								<div class="detallesli">
									<ul style="text-align: right;width:90%;" class="font2">
										<li style="font-weight: bold;width:20%">1/10</li>
										<li style="font-weight: bold;width:36%">1500.00</li>
										<li style="font-weight: bold;width:36%">0.00</li>
									</ul>
									
								</div>
								<ul style="margin-top: 5px;text-align: left;font-weight: bold; border-top: 1px dashed;" class="font2">
									<li id="licon_cob">PAGO CUOTA 1/10, 2/10, 12/10</li>
								</ul>
								<ul style="margin-bottom: 2px;text-align: left;font-weight: bold; border-bottom: 1px dashed;" class="font2">
									<li id="limon_let">CUATRO MIL QUINIENTOS PESOS</li>
								</ul>
								<ul style="margin-top: 2px;margin-bottom: -5px;text-align: left;font-weight: bold; border-top: 1px dashed;" class="font2">
									<li></li>
								</ul>
								<ul style="text-align: right;width:95%;" class="font2">
									<li style="font-weight: bold;width:100px;">Sub Total:</li>
									<li style="width:60px;" id="lisub_tot">4,500.00</li>
								</ul>
								<ul style="text-align: right;width:95%;" class="font2">
									<li style="font-weight: bold;width:100px;">Mora:</li>
									<li style="width:60px;" id="limor_cob_cob">0.00</li>
								</ul>
								<ul style="text-align: right;width:95%;" class="font2">
									<li style="font-weight: bold;width:100px;">Valor Pagado:</li>
									<li style="width:60px;" id="litot_cob">4,500.00</li>
								</ul>
								<ul style="text-align: right;width:95%;" class="font2">
									<li style="font-weight: bold;width:100px;">Balance:</li>
									<li style="width:60px;" id="libal_pre">9,500.00</li>
								</ul>
								<ul style="margin-top: 8px;text-align: center;font-weight: bold; " class="font1">
									<li>PAGOS CON RETRASO GENERAN MORA</li>
								</ul>
								<ul style="text-align: center;font-weight: bold; " class="font1">
									<li>VERIFIQUE SU RECIBO</li>
								</ul>
							</div>	
							</fieldset>
						</div>
					</div>
				</div>
			</section>
		</section>
		<a href="#" class="hide nav-off-screen-block" data-toggle="class:nav-off-screen" data-target="#nav"></a>
		</section>    <!-- /.vbox -->
		<script type="text/x-handlebars-template" id="Cuotas2MasterTemplate">
	      {{#each cuotas}}
	           	<tr>
					<td style="font-weight: bold;width: 85px;padding: 8px 4px;">{{ cuo_cuo }}</td>
					<td style="font-weight: bold;width: 100px;padding: 8px 4px;">{{ fec2_cuo }}</td>
					<td style="font-weight: bold;width: 97px;padding: 8px 4px;">{{ mor_cuo }}</td>
					<td style="font-weight: bold;width: 102px;padding: 8px 4px;">{{ pen_cuo }}</td>
					<td style="font-weight: bold;width: 110px;padding: 8px 4px;">{{ tot_cob }}</td>
				</tr>
	        
	      {{/each}}		   
	  	</script>
	  	
	  	<script type="text/x-handlebars-template" id="CuotasMasterTemplate">
	      {{#each cuotas}}
	           	<tr>
					<td style="font-weight: bold;width: 85px;padding: 8px 4px;">{{ cuo_cuo }}</td>
					<td style="font-weight: bold;width: 100px;padding: 8px 4px;">{{ fec2_cuo }}</td>
					<td style="font-weight: bold;width: 97px;padding: 8px 4px;">{{ mor_cuo }}</td>
					<td style="font-weight: bold;width: 102px;padding: 8px 4px;">{{ pen_cuo }}</td>
					<td style="font-weight: bold;width: 110px;padding: 2px 3px 0px 0px;">
						<div class="form-group input-group-sm">
							<input name="ref_cob" id="ref_cob" type="number" class="form-control MON_PFAC" data-next='nom_cli' data-bal="{{ pen_cuo }}" data-mor="{{ mor_cuo }}" data-fac="{{ cuo_cuo }}" data-fec="{{ fec_cuo }}" data-fec2="{{ fec2_cuo }}" data-orden="{{ id }}">
						</div>
					</td> 
				</tr>
	        
	      {{/each}}		   
	  	</script>
		  	
		<script type="text/javascript">
			$(document).ready(function () {			
				
		    	selectizeprestamo = {};
				$.getJSON('prestamos-sel', {bal_pre:'-5'}, function(data) {
					prestamo = data;
					selectizeprestamo = $('#pre_cob').selectizeThis({
						maxItems   : 1,
						valueField : 'num_pre',
						labelField : 'nom_cli',
						searchField: 'searc_field',
						options    : prestamo,
						create     : false,
						maxOptions : 10
					});
					limpiar();
					inhabilitarsel();
					buscarreg('1', 'DESC');
				});
						

			});
		</script>
		<script type="text/javascript" src="/js/cobrosm.js" mce_src="/js/cobrosm.js"></script>
		<script type="text/javascript" src="/js/numerosletra2.js" mce_src="/js/numerosletra2.js"></script>
		<script src="/js/filesaver.js" type="text/javascript"></script>
		<script src="/js/html2canvas.js" type="text/javascript"></script>
		<?php
		include "footer.php";
		?>