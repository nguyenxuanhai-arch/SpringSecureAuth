{pkgs}: {
  deps = [
    pkgs.postgresql
    pkgs.openjdk11
    pkgs.wget
    pkgs.maven
  ];
}
