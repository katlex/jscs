publishTo <<= (publishTo, version) { (unchanged, version) =>
  val worksForUser = Set("alun","lele")
  val worksForMe = for {
    myName <- sys.props.get("user.name")
    if worksForUser(myName)
  } yield true
  if (worksForMe getOrElse false) {
    val isSnapshot = version.toLowerCase.indexOf("snapshot") != -1
    val subRepo = if (isSnapshot) "snapshots" else "releases"
    val repo = Path.userHome / "katlex.github.com" / "maven2" / subRepo
    Some(Resolver.file("Local katlex repo", repo))
  } else unchanged
}
