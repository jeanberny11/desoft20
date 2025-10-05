<?php session_start();

include "include/db.php";


header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Headers: X-API-KEY, Origin, X-Requested-With, Content-Type, Accept, Access-Control-Request-Method");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT");
header("Allow: GET, POST, OPTIONS, PUT");


$datas  = array();

$bal=0;

if(isset($_POST['bal_pre'])){
	$bal=$_POST['bal_pre'];
}

$sql = "SELECT
    dat_pre.num_pre,
	dat_pre.cli_pre,
	dat_pre.bal_pre,
	dat_pre.mon_pre, 
	dat_cli.cod_cli,
	dat_cli.nom_cli,
	dat_cli.cobr_cli,
	dat_cli.ced_cli,
	dat_cli.bal_cli,
	dat_cli.tel1_cli	
FROM
    dat_pre
    LEFT OUTER JOIN 
    dat_cli
    ON dat_pre.cli_pre = dat_cli.cod_cli
    where bal_pre>$bal ";

$datas= buscar_db($sql);
foreach ($datas as $i => $value) {	
	$datas[$i]['nom_cli'] = $datas[$i]['num_pre'].' - '.$datas[$i]['nom_cli'];
	$datas[$i]['searc_field']= $datas[$i]['nom_cli'].' '.$datas[$i]['ced_cli'].' '.$datas[$i]['num_pre'];
}

echo json_encode($datas);

?>