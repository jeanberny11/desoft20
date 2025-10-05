<?php session_start();


include "include/db.php";


header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Headers: X-API-KEY, Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Method");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT");
header("Allow: GET, POST, OPTIONS, PUT");

$hoyp= date("Y-m-d");

$datas  = array();

$mora = (2)/100;

$reditos =0;
$tiempo =0;


$sqlpre = "SELECT 
		* 
	FROM 
		dat_pre
	WHERE 
		bal_pre >0 ";
$datapre= buscar_db($sqlpre);

$numpre ="";

foreach ($datapre as $key => $value) {
	$numpre =$value['num_pre'];
	$sqlmor = "SELECT 
					SUM( mora_cob ) AS mora
				FROM 
					dat_cob
				WHERE pre_cob ='$numpre' ";
	$datamor= buscar_db($sqlmor);

	if($value['tip_pre']=="3"){
		$ww = date("Y-m-d");
		$fecpre = $$value['fec_pre'];

		$diaspre = (strtotime($ww)-strtotime($fecpre))/(60 * 60 * 24);
		$tiempo = ceil($diaspre/$value['pla_pre'])-1;
		$reditos = $tiempo * $value['int_pre']- $datamor[0]["mora"];
	}


	$sql = "SELECT 
			* 
		FROM 
			dat_dpre
		WHERE 
			num_pre ='$numpre' and bal_cuo > 0
		ORDER BY 
			cod_dpre ASC ";

	$datas= buscar_db($sql);
	$fecha = date("Y-m-d");
	
	foreach ($datas as $i => $valu) {
		$diasv = (strtotime($hoyp)-strtotime($datas[$i]['fec_cuo']))/(60 * 60 * 24);
		//$diasv -= $datas[$i]['pla_fac'];
		if($diasv<0){
			$diasv = 0;
		}
		$datas[$i]['id']=$i+1;
		$datas[$i]['cuotas']=count($datas);
		$datas[$i]['dias_cuo']=$diasv;

		
		$fecha2 = date("d/m/y", strtotime($datas[$i]['fec_cuo']));
		$datas[$i]['fec_cuo']=$fecha2;	
		

		$datas[$i]['mor_cob']=round($datas[$i]['bal_cuo']*($mora* ($diasv-0) ), 2);
		

		$sqdcob="SELECT sum(mor_cob)as mora from dat_dcob where num_pre='".$valu['num_pre']."' and cuo_cuo='".$valu['cuo_cuo']."'  ";
		$sqdcob=buscar_db($sqdcob);
		if(count($sqdcob)>0){
			$datas[$i]['mor_cob']=$datas[$i]['mor_cob']- $sqdcob[0]['mora'];
		}
		


		if($datas[$i]['mor_cob']<0){
			$datas[$i]['mor_cob']=0.00;
		}
		/*$datas[$i]['mor_cob']=0.00;

		if($diasv>=3 and $_POST['mora']=="1"){
			$datas[$i]['mor_cob']=100.00;
		}*/
		$datas[$i]['reditos']=$reditos;
		$datas[$i]['tiempo']=$tiempo;
		if($reditos > 0){
			$datas[$i]['mor_cob']=$reditos;
		}
		$datas[$i]['pen_cuo']=$datas[$i]['bal_cuo']+$datas[$i]['mor_cob'];
		$datas[$i]['pen_cuo']=number_format($datas[$i]['pen_cuo'], 2, '.', '');

		$datas[$i]['pen_cuo2']=$datas[$i]['bal_cuo'];
		$datas[$i]['pen_cuo2']=number_format($datas[$i]['pen_cuo2'], 2, '.', '');
		unset($datas[$i]['fec2_cuo']);
	}

	$datapre[$key]['cobros-f']=$datas;
}


echo json_encode($datapre);
?>