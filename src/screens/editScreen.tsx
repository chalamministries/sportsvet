import React, { useState } from 'react';
import { SafeAreaView, StyleSheet, Text, View, ScrollView, Image, TouchableOpacity, ToastAndroid } from 'react-native';
import SegmentedControlTab from 'react-native-segmented-control-tab';
import {TouchableHighlight} from 'react-native-gesture-handler';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { Dimensions } from 'react-native';
import {PermissionsAndroid} from 'react-native';
import * as ImagePicker from 'react-native-image-picker';

const win = Dimensions.get('window');

const EditScreen = ({ navigation }: any) => {

  const [selectedIndex, setSelectedIndex] = useState(0);
  const [selectedIndices, setSelectedIndices] = useState([0]);
  const [customStyleIndex, setCustomStyleIndex] = useState(0);
  const [flexDirection, setflexDirection] = useState("row");


  const handleSingleIndexSelect = (index:any) => {
    // For single Tab Selection SegmentedControlTab
    setSelectedIndex(index);
  };

  const handleMultipleIndexSelect = (index:any) => {
    // For multi Tab Selection SegmentedControlTab
    if (selectedIndices.includes(index)) {
      console.log(selectedIndices.filter((i) => i !== index));
      //if included in the selected array then remove
      setSelectedIndices(selectedIndices.filter((i) => i !== index));
    } else {
      //if not included in the selected array then add
      setSelectedIndices([...selectedIndices, index]);
    }
  };

  const handleCustomIndexSelect = (index:any) => {
    //handle tab selection for custom Tab Selection SegmentedControlTab
    setCustomStyleIndex(index);
  };


  const requestCameraPermission = async () => {
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.CAMERA,
        {
          title: "App Camera Permission",
          message:"App needs access to your camera",
          buttonNeutral: "Ask Me Later",
          buttonNegative: "Cancel",
          buttonPositive: "OK"
        }
      );
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        console.log("Camera permission given");
        recordVideo()
      } else {
        console.log("Camera permission denied");
      }
    } catch (err) {
      console.warn(err);
    }
  };

  const recordVideo = () => {
    let options:any = {
      storageOptions: {
        skipBackup: true,
        path: 'images',
      },
    };
    ImagePicker.launchCamera(options, (res:any) => {
      console.log('Response = ', res);
      if (res.didCancel) {
        console.log('User cancelled image picker');
      } else if (res.error) {
        console.log('ImagePicker Error: ', res.error);
      } else if (res.customButton) {
        console.log('User tapped custom button: ', res.customButton);
        ToastAndroid.show(res.customButton, ToastAndroid.SHORT);
      } else {
        const source = { uri: res.uri };
        console.log('response', JSON.stringify(res));
      }
    });

  }

  return (
    <SafeAreaView style={styles.pageBg}>
      <ScrollView>
       <TouchableHighlight
        underlayColor={'transparent'}>
         <View style={styles.upperlayer}>
          <TouchableOpacity onPress={() => {
             navigation.pop()
           }}>
           <Ionicons
            name={'arrow-back'}
            style={styles.headerIconStyleRight}
            />
            </TouchableOpacity>
          <Text style={styles.tagTitle}>Select Video</Text>
          <TouchableOpacity onPress={() => {
             requestCameraPermission()
           }}>
          <Ionicons
            name={'ios-videocam-outline'}
            style={styles.headerIconStyle}
          />
          </TouchableOpacity>
        </View>
      </TouchableHighlight>
      <View style={styles.container}>
      <SegmentedControlTab
          values={['All Videos', 'Album']}
          selectedIndex={customStyleIndex}
          onTabPress={handleCustomIndexSelect}
          borderRadius={0}
          allowFontScaling={true}
          tabsContainerStyle={{ height: 50, backgroundColor: 'white' }}
          tabStyle={{
            backgroundColor: 'transparent',
            borderColor:'transparent',
          }}
          activeTabStyle={{ 
            backgroundColor: 'white', 
            marginTop: 2,
            borderBottomWidth: 2,
            borderBottomColor: '#5b57cf',
          }}
          tabTextStyle={{ color: '#888888', fontWeight: 'bold' }}
          activeTabTextStyle={{ color: '#5b57cf', fontWeight: 'bold' }}
        />
        {customStyleIndex === 0 && (
           <TouchableOpacity
           onPress={() => {
             navigation.navigate('Editor')
           }}>
            <Image
                style={styles.imageView}
                source={require('../assets/background/videos.png')}
                resizeMode={'contain'}/>
            </TouchableOpacity>
        )}
        {customStyleIndex === 1 && (
          <TouchableOpacity
          onPress={() => {
            navigation.navigate('Editor')
          }}>
          <Image 
              style={styles.imageView}
              source={require('../assets/background/albums.png')}
              resizeMode={'contain'}/>
          </TouchableOpacity>
        )}
        </View>
     </ScrollView>
    </SafeAreaView>
  );
};

export default EditScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'white',
    padding: 10,
  },
  tagTitle:{
    fontWeight: 'bold',
    fontSize: 18
  },
  imageView:{
    justifyContent: 'center',
    alignSelf: 'center', 
    width: win.width,
  },
  pageBg:{
    backgroundColor: 'white',
  },
  headerText: {
    padding: 8,
    fontSize: 14,
    color: '#444444',
    textAlign: 'center',
  },
  tabContent: {
    color: 'black',
    fontSize: 18,
    margin: 24,
  },
  seperator: {
    marginHorizontal: -10,
    alignSelf: 'stretch',
    borderTopWidth: 1,
    borderTopColor: '#888888',
    marginTop: 24,
  },
  tabStyle: {
    borderColor: '#D52C43',
  },
  activeTabStyle: {
    backgroundColor: '#D52C43',
  },
  upperlayer: {
    paddingTop: 8,
    flexDirection:'row',
    justifyContent:'space-between'
  },
 headerIconStyle: {
    color: 'black',
    fontSize: 26,
    paddingRight:8,
  },
  headerIconStyleRight:{
    color: 'black',
    fontSize: 26,
    paddingLeft:8,
  }
});
