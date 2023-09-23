#
# build contaner for GitHub Codespaces
#
sudo apt-get -y update
sudo apt-get -y upgrade
sudo apt-get -y install language-pack-ja graphviz fontconfig fonts-noto* language-selector-common
sudo update-locale LANG=ja_JP.UTF8
echo 'export LANG=ja_JP.UTF-8' >> ~/.bashrc
export LANG=ja_JP.UTF-8
fc-cache -f
