#!/bin/bash
# 当前目录
cur_dir=$PWD
# 被复制项目的路径
src=$cur_dir/$1
# 复制项目的路径
dest=$cur_dir/$2
# 被复制项目的包名
src_package_name=$3
# 复制项目的包名
dest_package_name=$4

echo -e "copy project: \n$src --> \n$dest"
echo -e "rename package name: \n$src_package_name --> \n$dest_package_name"

# 1. 整个项目复制 project_path
echo -e "\n########## copy whole project #############\n"
#** step 1
cp -r $src $dest

# 2. 代码包名修改 package_path
echo -e "\n########## rename package path #############\n"

# 在以复制项目下 寻找原项目的包名路径
for src_package_path in `find $dest -name $src_package_name | grep src`; do
    # 去除原包名
    src_package_dir=`echo ${src_package_path%/*}`
    # 添加所需包名
    dest_package_path=$src_package_dir/$dest_package_name

    echo -e "rename package name: \n$src_package_path --> \n$dest_package_path"
    #** step 2
    mv $src_package_path $dest_package_path
done

# 3. 被修改包名目录下的文件package修改 file_name
echo -e "\n########## rename java file package path #############\n"
# 文件内容 点包名
default_dot_package_name="com.lingsh.android";
src_dot_package_name=$default_dot_package_name.$src_package_name
dest_dot_package_name=$default_dot_package_name.$dest_package_name

echo -e "dot package name: \n$src_dot_package_name --> \n$dest_dot_package_name"
# 搜索复制项目下需要修改的java文件
for dest_package_path in `find $dest -name $dest_package_name | grep src`; do
    echo -e "cur find path: $dest_package_path\n"
    for java_file_path in `find $dest_package_path -name *.java`; do
        echo -e "found: $java_file_path\n"
        #** step 3
        # 修改java内容 点包名
        sed -i "s/$src_dot_package_name/$dest_dot_package_name/g" $java_file_path
        head $java_file_path -n 1
    done
done

# 4. 修改app目录下的build.gradle app_build_gradle_path
echo -e "\n########## rename build gradle path #############\n"

app_path=`find $dest -name app`
app_build_gradle_path=$app_path/build.gradle
echo -e "\napp gradle: $app_build_gradle_path\n"
#** step 4
sed -i "s/$src_dot_package_name/$dest_dot_package_name/g" $app_build_gradle_path

# 5. 修改app目录下的manifest.xml manifest_path
echo -e "\n########## rename AndroidManifest.xml path #############\n"

manifest_path=`find $dest -name AndroidManifest.xml | grep src`
echo -e "\nmanifeat.xml path: $manifest_path\n"
#** step 5
sed -i "s/$src_dot_package_name/$dest_dot_package_name/g" $manifest_path

