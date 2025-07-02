app_path=$0

while
    APP_HOME=${app_path%"${app_path##*/}"}
    [ -h "$app_path" ]
do
    ls=$( ls -ld "$app_path" )
    link=${ls#*' -> '}
    case $link in
      /*)   app_path=$link ;;
      *)    app_path=$APP_HOME$link ;;
    esac
done

APP_BASE_NAME=${0##*/}
APP_HOME=$( cd "${APP_HOME:-./}" && pwd -P ) || exit

# Проверяем, является ли команда 'build'
is_build=false
for arg in "$@"; do
    if [ "$arg" = "build" ]; then
        is_build=true
        break
    fi
done

# Если команда 'build', запрашиваем тип версионирования
if [ "$is_build" = true ]; then
    version_file="$APP_HOME/version.txt"
    
    # Чтение текущей версии
    if [ -f "$version_file" ]; then
        version=$(cat "$version_file")
    else
        version="1.0.0"
    fi
    
    # Разбиваем версию на компоненты
    IFS='.' read -r major minor patch <<< "$version"
    
    echo "Текущая версия: $version"
    echo "Выберите тип версионирования:"
    echo "0 - Увеличить PATCH версию ($major.$minor.$(($patch + 1)))"
    echo "1 - Увеличить MINOR версию ($major.$(($minor + 1)).0)"
    echo "2 - Увеличить MAJOR версию ($(($major + 1)).0.0)"
    
    read -p "Введите номер (0-2): " version_type
    
    # Проверка ввода
    if [ "$version_type" != "0" ] && [ "$version_type" != "1" ] && [ "$version_type" != "2" ]; then
        echo "Неверный ввод, используется PATCH (0)"
        version_type=0
    fi
    
    # Увеличиваем версию в зависимости от типа
    case $version_type in
        2) # Увеличение MAJOR версии
            major=$(($major + 1))
            minor=0
            patch=0
            ;;
        1) # Увеличение MINOR версии
            minor=$(($minor + 1))
            patch=0
            ;;
        0|*) # Увеличение PATCH версии
            patch=$(($patch + 1))
            ;;
    esac
    
    # Записываем новую версию в файл
    new_version="$major.$minor.$patch"
    echo "$new_version" > "$version_file"
    echo "Новая версия: $new_version"
fi

MAX_FD=maximum

warn () {
    echo "$*"
} >&2

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

cygwin=false
msys=false
darwin=false
nonstop=false
case "$( uname )" in
  CYGWIN* )         cygwin=true  ;;
  Darwin* )         darwin=true  ;;
  MSYS* | MINGW* )  msys=true    ;;
  NONSTOP* )        nonstop=true ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD=java
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

if ! "$cygwin" && ! "$darwin" && ! "$nonstop" ; then
    case $MAX_FD in
      max*)
        MAX_FD=$( ulimit -H -n ) ||
            warn "Could not query maximum file descriptor limit"
    esac
    case $MAX_FD in
      '' | soft) ;;
      *)
        ulimit -n "$MAX_FD" ||
            warn "Could not set maximum file descriptor limit to $MAX_FD"
    esac
fi

if "$cygwin" || "$msys" ; then
    APP_HOME=$( cygpath --path --mixed "$APP_HOME" )
    CLASSPATH=$( cygpath --path --mixed "$CLASSPATH" )

    JAVACMD=$( cygpath --unix "$JAVACMD" )

    for arg do
        if
            case $arg in
              -*)   false ;;
              /?*)  t=${arg#/} t=/${t%%/*}
                    [ -e "$t" ] ;;
              *)    false ;;
            esac
        then
            arg=$( cygpath --path --ignore --mixed "$arg" )
        fi
        shift
        set -- "$@" "$arg"
    done
fi

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

set -- \
        "-Dorg.gradle.appname=$APP_BASE_NAME" \
        -classpath "$CLASSPATH" \
        org.gradle.wrapper.GradleWrapperMain \
        "$@"

if ! command -v xargs >/dev/null 2>&1
then
    die "xargs is not available"
fi

eval "set -- $(
        printf '%s\n' "$DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS" |
        xargs -n1 |
        sed ' s~[^-[:alnum:]+,./:=@_]~\\&~g; ' |
        tr '\n' ' '
    )" '"$@"'

exec "$JAVACMD" "$@"
