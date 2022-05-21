import {useNavigation} from '@react-navigation/native';
import React from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ImageBackground,
  Dimensions,
  StyleSheet,
  Keyboard,
  TouchableHighlight,
  ToastAndroid,
  Button
} from 'react-native';
import {StackNavigationProp} from '@react-navigation/stack';
import {RootStackParamList} from './RootStackPrams';

const {width, height} = Dimensions.get('screen');
type mainScreenProp = StackNavigationProp<RootStackParamList, 'Edit'>;

function EditScreen() {
  const navigation = useNavigation<mainScreenProp>();


  return (
    <ImageBackground source={require("../assets/background/bg1.png")} style={{width:width,height:height}}>
    <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
      <Text>Main Screen</Text>
      <Button title="Login" onPress={() => navigation.navigate('Home')} />
    </View>
    </ImageBackground>
  );


}

export default EditScreen;


