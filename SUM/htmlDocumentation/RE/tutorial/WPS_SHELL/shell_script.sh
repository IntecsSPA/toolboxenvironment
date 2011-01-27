
dir_out=$OUTPUT_REPOSITORY/

f_in=$InputPollutant
f_out=$OUTPUT_REPOSITORY/$OutputRiskIndex

sout=$OUTPUT_REPOSITORY/"execution.log"


echo $f_in $f_out >> $sout

# rajout metadonnees

    ncwa -O -y max -v longicrs $f_in temp.nc
    max_loncrs=$(ncks -C -H -s "%f\n" -v longicrs temp.nc)
    ncwa -O -y min -v longicrs $f_in temp.nc    
    min_loncrs=$(ncks -C -H -s "%f\n" -v longicrs temp.nc)
    ncwa -O -y max -v latitcrs $f_in temp.nc
    max_latcrs=$(ncks -C -H -s "%f\n" -v latitcrs temp.nc)
    ncwa -O -y min -v latitcrs $f_in temp.nc    
    min_latcrs=$(ncks -C -H -s "%f\n" -v latitcrs temp.nc)
    ncwa -O -y min -v time $f_in temp.nc
    min_time=$(ncks -C -H -s "%i\n" -v time temp.nc)
    ncwa -O -y max -v time $f_in temp.nc
    max_time=$(ncks -C -H -s "%i\n" -v time temp.nc)
    ncwa -O -y min -v day $f_in temp.nc
    min_day=$(ncks -C -H -s "%i\n" -v day temp.nc)
    ncwa -O -y max -v day $f_in temp.nc
    max_day=$(ncks -C -H -s "%i\n" -v day temp.nc)
rm temp.nc
# Definition des cas et coefficients associes pour une ZONE DONNEE

#CAS1: Age=0-9(1); Sexe=F(1); Pathologie=cardiovasculaire(1);  

a=`echo "(1.02*1.912)" | bc -l` #1.912 est le coefficient de conversion de ppb en µg/m3 du NO2
echo $a >> $sout
b=`echo "(1.06*1.995)" | bc -l` #1.995 est le coefficient de conversion de ppb en µg/m3 du O3
c=`echo "(1.10*1.800)" | bc -l` #A VERIFIER 1.800 est le coefficient de conversion de ppb en µg/m3 du PM10


formule1="ARI_A1S1P1="$a"*MOYNO2+"$b"*MOYO3+"$c"*MAXPM10"
formule2="NO2_A1S1P1="$a"*MOYNO2/ARI_A1S1P1"
formule3="O3_A1S1P1="$b"*MOYO3/ARI_A1S1P1"
formule4="PM10_A1S1P1="$c"*MAXPM10/ARI_A1S1P1"


echo $formule1 >> $sout

ncap -O -v -s $formule1 $f_in $f_out
ncap -v -A -s $formule2 $f_in $f_out
ncap -v -A -s $formule3 $f_in $f_out
ncap -v -A -s $formule4 $f_in $f_out


#CAS2: Age=0-9(1); Sexe=M(2); Pathologie=cardiovasculaire(1);  

a=`echo "(2.02*1.912)" | bc -l`
b=`echo "(1.00*1.995)" | bc -l`
c=`echo "(1.10*1.800)" | bc -l`

formule1="ARI_A1S2P1="$a"*MOYNO2+"$b"*MOYO3+"$c"*MAXPM10"

formule2="NO2_A1S2P1="$a"*MOYNO2/ARI_A1S2P1"
formule3="O3_A1S2P1="$b"*MOYO3/ARI_A1S2P1"
formule4="PM10_A1S2P1="$c"*MAXPM10/ARI_A1S2P1"

echo $formule1 >> $sout

ncap -v -A -s $formule1 $f_in $f_out
ncap -v -A -s $formule2 $f_in $f_out
ncap -v -A -s $formule3 $f_in $f_out
ncap -v -A -s $formule4 $f_in $f_out

#CAS3: Age=10-19(2); Sexe=F(1); Pathologie=cardiovasculaire(1);  

a=`echo "(1.22*1.912)" | bc -l`
b=`echo "(1.33*1.995)" | bc -l`
c=`echo "(1.11*1.800)" | bc -l`

formule1="ARI_A2S1P1="$a"*MOYNO2+"$b"*MOYO3+"$c"*MAXPM10"

formule2="NO2_A2S1P1="$a"*MOYNO2/ARI_A2S1P1"
formule3="O3_A2S1P1="$b"*MOYO3/ARI_A2S1P1"
formule4="PM10_A2S1P1="$c"*MAXPM10/ARI_A2S1P1"

echo $formule1 >> $sout

ncap -v -A -s $formule1 $f_in $f_out
ncap -v -A -s $formule2 $f_in $f_out
ncap -v -A -s $formule3 $f_in $f_out
ncap -v -A -s $formule4 $f_in $f_out

ncks -A -v day  $f_in $f_out
ncks -A -v time $f_in $f_out
ncks -A -v longicrs $f_in $f_out 
ncks -A -v latitcrs $f_in $f_out 

ncatted -O -a source,global,o,c,'CHIMERE' -a earth_radius,global,o,c,'6378.137' -a earth_radius_unit,global,o,c,'km' -a valid_min,latitcrs,o,c,$min_latcrs -a valid_max,latitcrs,o,c,$max_latcrs -a valid_min,longicrs,o,c,$min_loncrs -a valid_max,longicrs,o,c,$max_loncrs -a units,longicrs,o,c,degrees_east  -a units,latitcrs,o,c,degrees_north -a long_name,ARI_A1S2P1,o,c,"ARI for Age:0-9y, Sex:M, Pathology:Cardio-V." -a long_name,ARI_A2S1P1,o,c,"ARI for Age:10-19y, Sex:F, Pathology:Cardio-V." -a long_name,NO2_A2S1P1,o,c,"Contribution of NO2 in ARI_A2S1P1" -a unit,NO2_A2S1P1,o,c,"%" -a long_name,NO2_A1S1P1,o,c,"Contribution of NO2 in ARI_A1S1P1" -a unit,NO2_A1S1P1,o,c,"%" -a long_name,NO2_A1S2P1,o,c,"Contribution of NO2 in ARI_A1S2P1" -a unit,NO2_A1S2P1,o,c,"%" -a long_name,O3_A1S1P1,o,c,"Contribution of O3 in ARI_A1S1P1" -a unit,O3_A1S1P1,o,c,"%" -a long_name,O3_A2S1P1,o,c,"Contribution of O3 in ARI_A2S1P1" -a unit,O3_A2S1P1,o,c,"%" -a long_name,O3_A1S2P1,o,c,"Contribution of O3 in ARI_A1S2P1" -a unit,O3_A1S2P1,o,c,"%"  -a long_name,PM10_A1S1P1,o,c,"Contribution of PM10 in ARI_A1S1P1" -a unit,PM10_A1S1P1,o,c,"%" -a long_name,PM10_A2S1P1,o,c,"Contribution of PM10 in ARI_A2S1P1" -a unit,PM10_A2S1P1,o,c,"%" -a long_name,PM10_A1S2P1,o,c,"Contribution of PM10 in ARI_A1S2P1" -a unit,PM10_A1S2P1,o,c,"%" -a valid_min,time,o,c,$min_time  -a valid_max,time,o,c,$max_time   -a long_name,ARI_A1S1P1,o,c,"ARI for Age:0-9y, Sex:F, Pathology:Cardio-V." -a valid_min,day,o,c,$min_day  -a valid_max,day,o,c,$max_day $f_out temp.nc

mv temp.nc $f_out 

rm -f $f_in
