#!/system/bin/sh

#onst struct config_control rt5645_playback_off_controls[] = {
ctl_name="SPKVOL L Switch"
int_val=0  #{off}
tinymix "$ctl_name" $int_val

ctl_name="SPKVOL R Switch"
int_val=0  #{off}
tinymix "$ctl_name" $int_val

ctl_name="SPOL MIX SPKVOL L Switch"
int_val=0  #{off}
tinymix "$ctl_name" $int_val

ctl_name="SPOR MIX SPKVOL R Switch"
int_val=0  #{off}
tinymix "$ctl_name" $int_val

ctl_name="Speaker Channel Switch"
int_val="0 0"  #{off,off}
tinymix "$ctl_name" $int_val



#const struct config_control rt5645_main_mic_capture_controls[] = {
#set -x


ctl_name="Stereo1 DMIC Mux"
str_val="DMIC1"
tinymix "$ctl_name" $str_val

ctl_name="Stereo1 ADC2 Mux"
str_val="DMIC"
tinymix "$ctl_name" $str_val


ctl_name="Sto1 ADC MIXL ADC1 Switch"
int_val=0 #{off}
tinymix "$ctl_name" $int_val

ctl_name="Sto1 ADC MIXR ADC1 Switch"
int_val=0 #{off}
tinymix "$ctl_name" $int_val

ctl_name="Sto1 ADC MIXL ADC2 Switch"
int_val=1 #{on}
tinymix "$ctl_name" $int_val

ctl_name="Sto1 ADC MIXR ADC2 Switch"
int_val=1 #{on}
tinymix "$ctl_name" $int_val

if false; then
#//min=0,max=8, bypass=0=0db, 30db=3, 52db=8
#/* {
#       .ctl_name="IN1 Boost"
#       .int_val={5}
#   }
#*/
true
fi

#   //dBscale-min=-17.625dB,step=0.375dB,min=0,max=127

ctl_name="ADC Capture Volume"
int_val="85 85"
tinymix "$ctl_name" $int_val


i2cset -f -y 1 0x1a 0x1c 0x7f7f w
i2cset -f -y 1 0x1a 0x83 0x8008 w






